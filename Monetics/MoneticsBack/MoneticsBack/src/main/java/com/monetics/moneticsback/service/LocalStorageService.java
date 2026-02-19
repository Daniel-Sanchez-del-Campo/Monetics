package com.monetics.moneticsback.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class LocalStorageService {

    @Value("${app.uploads.path:uploads/tickets}")
    private String uploadsPath;

    private Path uploadsDir;

    @PostConstruct
    public void init() throws IOException {
        this.uploadsDir = Paths.get(uploadsPath).toAbsolutePath().normalize();
        Files.createDirectories(uploadsDir);
    }

    public String guardarArchivo(MultipartFile archivo, String nombreArchivo) throws IOException {
        Path destino = uploadsDir.resolve(nombreArchivo).normalize();
        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        return nombreArchivo;
    }

    public Resource cargarArchivo(String nombreArchivo) throws IOException {
        Path filePath = uploadsDir.resolve(nombreArchivo).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) {
            throw new IOException("Archivo no encontrado: " + nombreArchivo);
        }
        return resource;
    }

    public void eliminarArchivo(String nombreArchivo) {
        try {
            Path filePath = uploadsDir.resolve(nombreArchivo).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Error eliminando archivo local: " + e.getMessage());
        }
    }
}
