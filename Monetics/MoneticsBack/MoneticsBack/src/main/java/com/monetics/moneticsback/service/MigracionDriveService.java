package com.monetics.moneticsback.service;

import com.google.api.services.drive.model.File;
import com.monetics.moneticsback.model.Gasto;
import com.monetics.moneticsback.repository.GastoRepository;
import com.google.api.services.drive.Drive;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

/**
 * Servicio para migrar imagenes existentes (Base64 en BD) a Google Drive.
 * USAR UNA SOLA VEZ y luego eliminar o desactivar.
 */
@Service
@ConditionalOnBean(Drive.class)
public class MigracionDriveService {

    private final GastoRepository gastoRepository;
    private final GoogleDriveService googleDriveService;

    public MigracionDriveService(
            GastoRepository gastoRepository,
            GoogleDriveService googleDriveService
    ) {
        this.gastoRepository = gastoRepository;
        this.googleDriveService = googleDriveService;
    }

    @Transactional
    public int migrarImagenesADrive() {
        List<Gasto> gastosPendientes = gastoRepository.findAll().stream()
                .filter(g -> g.getImagenTicket() != null
                        && !g.getImagenTicket().isBlank()
                        && g.getDriveFileId() == null)
                .toList();

        int migrados = 0;

        for (Gasto gasto : gastosPendientes) {
            try {
                String base64 = gasto.getImagenTicket();

                String mimeType = "image/jpeg";
                String base64Data = base64;

                if (base64.startsWith("data:")) {
                    int commaIndex = base64.indexOf(",");
                    String header = base64.substring(0, commaIndex);
                    mimeType = header.replace("data:", "").replace(";base64", "");
                    base64Data = base64.substring(commaIndex + 1);
                }

                byte[] imageBytes = Base64.getDecoder().decode(base64Data);

                String extension = mimeType.contains("png") ? ".png" : ".jpg";
                String fileName = "ticket_migrado_" + gasto.getIdGasto() + extension;

                final String finalMimeType = mimeType;
                final byte[] finalBytes = imageBytes;
                final String finalName = fileName;
                MultipartFile multipartFile = new MultipartFile() {
                    public String getName() { return "archivo"; }
                    public String getOriginalFilename() { return finalName; }
                    public String getContentType() { return finalMimeType; }
                    public boolean isEmpty() { return finalBytes.length == 0; }
                    public long getSize() { return finalBytes.length; }
                    public byte[] getBytes() { return finalBytes; }
                    public InputStream getInputStream() { return new ByteArrayInputStream(finalBytes); }
                    public void transferTo(java.io.File dest) throws IOException {
                        java.nio.file.Files.write(dest.toPath(), finalBytes);
                    }
                };

                Long idUsuario = gasto.getUsuario().getIdUsuario();
                File driveFile = googleDriveService.subirArchivo(multipartFile, idUsuario, fileName);

                gasto.setDriveFileId(driveFile.getId());
                gasto.setDriveFileUrl(googleDriveService.obtenerUrlVisualizacion(driveFile.getId()));
                gasto.setImagenNombre(fileName);

                gasto.setImagenTicket(null);

                gastoRepository.save(gasto);
                migrados++;

                System.out.println("Migrado gasto #" + gasto.getIdGasto() + " a Drive: " + driveFile.getId());

            } catch (Exception e) {
                System.err.println("Error migrando gasto #" + gasto.getIdGasto() + ": " + e.getMessage());
            }
        }

        return migrados;
    }
}
