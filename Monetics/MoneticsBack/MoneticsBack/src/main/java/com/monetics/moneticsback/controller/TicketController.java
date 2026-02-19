package com.monetics.moneticsback.controller;

import com.monetics.moneticsback.dto.AnalisisTicketDTO;
import com.monetics.moneticsback.service.GoogleDriveService;
import com.monetics.moneticsback.service.LocalStorageService;
import com.monetics.moneticsback.service.TicketAnalysisService;
import com.monetics.moneticsback.service.MigracionDriveService;
import com.google.api.services.drive.model.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private GoogleDriveService googleDriveService;
    private MigracionDriveService migracionDriveService;
    private final TicketAnalysisService ticketAnalysisService;
    private final LocalStorageService localStorageService;

    public TicketController(TicketAnalysisService ticketAnalysisService, LocalStorageService localStorageService) {
        this.ticketAnalysisService = ticketAnalysisService;
        this.localStorageService = localStorageService;
    }

    @Autowired(required = false)
    public void setGoogleDriveService(GoogleDriveService googleDriveService) {
        this.googleDriveService = googleDriveService;
    }

    @Autowired(required = false)
    public void setMigracionDriveService(MigracionDriveService migracionDriveService) {
        this.migracionDriveService = migracionDriveService;
    }

    /**
     * Sube una imagen de ticket a Google Drive y guarda copia local.
     */
    @PostMapping("/subir-imagen")
    public ResponseEntity<Map<String, String>> subirImagen(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("idUsuario") Long idUsuario
    ) throws IOException {

        String contentType = archivo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Solo se permiten imagenes"));
        }

        if (archivo.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(Map.of("error", "La imagen no puede superar 5MB"));
        }

        String extension = archivo.getOriginalFilename() != null
                ? archivo.getOriginalFilename().substring(archivo.getOriginalFilename().lastIndexOf('.'))
                : ".jpg";
        String nombreArchivo = "ticket_" + System.currentTimeMillis() + extension;

        // Guardar copia local
        localStorageService.guardarArchivo(archivo, nombreArchivo);

        Map<String, String> response = new HashMap<>();
        response.put("imagenNombre", nombreArchivo);

        // Subir a Drive (si esta configurado)
        if (googleDriveService != null) {
            try {
                File driveFile = googleDriveService.subirArchivo(archivo, idUsuario, nombreArchivo);
                response.put("driveFileId", driveFile.getId());
                response.put("driveFileUrl", googleDriveService.obtenerUrlVisualizacion(driveFile.getId()));
            } catch (Exception e) {
                System.err.println("Error subiendo a Drive (copia local guardada): " + e.getMessage());
            }
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Sirve una imagen de ticket almacenada localmente.
     */
    @GetMapping("/imagen/{nombreArchivo}")
    public ResponseEntity<Resource> verImagen(@PathVariable String nombreArchivo) {
        try {
            Resource resource = localStorageService.cargarArchivo(nombreArchivo);

            String contentType = "image/jpeg";
            if (nombreArchivo.endsWith(".png")) {
                contentType = "image/png";
            } else if (nombreArchivo.endsWith(".webp")) {
                contentType = "image/webp";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombreArchivo + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina una imagen de Google Drive y del almacenamiento local.
     */
    @DeleteMapping("/imagen/{fileId}")
    public ResponseEntity<Void> eliminarImagen(
            @PathVariable String fileId,
            @RequestParam(value = "nombreArchivo", required = false) String nombreArchivo
    ) throws IOException {
        if (googleDriveService != null) {
            try {
                googleDriveService.eliminarArchivo(fileId);
            } catch (Exception e) {
                System.err.println("Error eliminando de Drive: " + e.getMessage());
            }
        }
        if (nombreArchivo != null && !nombreArchivo.isBlank()) {
            localStorageService.eliminarArchivo(nombreArchivo);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Analiza una imagen de ticket con IA y devuelve los datos extraidos.
     */
    @PostMapping("/analizar")
    public ResponseEntity<AnalisisTicketDTO> analizarTicket(
            @RequestParam("archivo") MultipartFile archivo
    ) {
        String contentType = archivo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().build();
        }

        try {
            AnalisisTicketDTO resultado = ticketAnalysisService.analizarTicket(archivo);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            System.err.println("Error al analizar ticket: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * ENDPOINT TEMPORAL - Ejecuta la migracion de imagenes Base64 a Drive.
     */
    @PostMapping("/migrar-a-drive")
    public ResponseEntity<Map<String, Object>> migrarADrive() {
        if (migracionDriveService == null) {
            return ResponseEntity.status(503).body(Map.of("error", "Google Drive no esta configurado"));
        }
        int migrados = migracionDriveService.migrarImagenesADrive();
        return ResponseEntity.ok(Map.of(
                "mensaje", "Migracion completada",
                "gastosMigrados", migrados
        ));
    }
}
