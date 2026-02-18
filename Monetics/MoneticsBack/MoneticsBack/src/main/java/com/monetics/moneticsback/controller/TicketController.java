package com.monetics.moneticsback.controller;

import com.monetics.moneticsback.dto.AnalisisTicketDTO;
import com.monetics.moneticsback.service.GoogleDriveService;
import com.monetics.moneticsback.service.TicketAnalysisService;
import com.monetics.moneticsback.service.MigracionDriveService;
import com.google.api.services.drive.model.File;
import org.springframework.beans.factory.annotation.Autowired;
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

    public TicketController(TicketAnalysisService ticketAnalysisService) {
        this.ticketAnalysisService = ticketAnalysisService;
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
     * Sube una imagen de ticket a Google Drive.
     */
    @PostMapping("/subir-imagen")
    public ResponseEntity<Map<String, String>> subirImagen(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("idUsuario") Long idUsuario
    ) throws IOException {

        if (googleDriveService == null) {
            return ResponseEntity.status(503).body(Map.of("error", "Google Drive no esta configurado"));
        }

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

        File driveFile = googleDriveService.subirArchivo(archivo, idUsuario, nombreArchivo);

        Map<String, String> response = new HashMap<>();
        response.put("driveFileId", driveFile.getId());
        response.put("driveFileUrl", googleDriveService.obtenerUrlVisualizacion(driveFile.getId()));
        response.put("imagenNombre", archivo.getOriginalFilename());

        return ResponseEntity.ok(response);
    }

    /**
     * Elimina una imagen de Google Drive.
     */
    @DeleteMapping("/imagen/{fileId}")
    public ResponseEntity<Void> eliminarImagen(@PathVariable String fileId) throws IOException {
        if (googleDriveService == null) {
            return ResponseEntity.status(503).build();
        }
        googleDriveService.eliminarArchivo(fileId);
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
