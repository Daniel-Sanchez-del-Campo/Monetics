package com.monetics.moneticsback.service;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@ConditionalOnBean(Drive.class)
public class GoogleDriveService {

    private final Drive driveService;

    @Value("${google.drive.folder-id}")
    private String rootFolderId;

    public GoogleDriveService(Drive driveService) {
        this.driveService = driveService;
    }

    /**
     * Sube un archivo a Google Drive en la carpeta compartida.
     */
    public File subirArchivo(MultipartFile archivo, Long idUsuario, String nombreArchivo) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(nombreArchivo);
        fileMetadata.setParents(Collections.singletonList(rootFolderId));

        InputStreamContent mediaContent = new InputStreamContent(
                archivo.getContentType(),
                archivo.getInputStream()
        );

        File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setSupportsAllDrives(true)
                .setFields("id, name, webViewLink, webContentLink")
                .execute();

        // Hacer el archivo publico para visualizacion
        try {
            Permission permission = new Permission();
            permission.setType("anyone");
            permission.setRole("reader");
            driveService.permissions().create(uploadedFile.getId(), permission)
                    .setSupportsAllDrives(true)
                    .execute();
        } catch (Exception e) {
            System.err.println("No se pudo hacer publico el archivo: " + e.getMessage());
        }

        return uploadedFile;
    }

    /**
     * Elimina un archivo de Google Drive por su ID.
     */
    public void eliminarArchivo(String fileId) throws IOException {
        driveService.files().delete(fileId)
                .setSupportsAllDrives(true)
                .execute();
    }

    /**
     * Obtiene la URL publica de visualizacion de un archivo.
     */
    public String obtenerUrlVisualizacion(String fileId) {
        return "https://drive.google.com/uc?id=" + fileId + "&export=view";
    }
}
