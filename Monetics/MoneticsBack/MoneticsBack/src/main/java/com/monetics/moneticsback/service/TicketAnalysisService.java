package com.monetics.moneticsback.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monetics.moneticsback.dto.AnalisisTicketDTO;
import com.monetics.moneticsback.model.Categoria;
import com.monetics.moneticsback.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketAnalysisService {

    @Value("${ia.gemini.api-key}")
    private String apiKey;

    @Value("${ia.gemini.model}")
    private String model;

    @Value("${ia.gemini.max-tokens}")
    private int maxTokens;

    @Value("${ia.gemini.timeout-seconds}")
    private int timeoutSeconds;

    private final CategoriaRepository categoriaRepository;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public TicketAnalysisService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Analiza una imagen de ticket usando Google Gemini Vision y extrae los datos.
     */
    public AnalisisTicketDTO analizarTicket(MultipartFile archivo) throws IOException, InterruptedException {

        List<Categoria> categorias = categoriaRepository.findByActivaTrue();
        String listaCategorias = categorias.stream()
                .map(c -> String.format("- ID: %d, Nombre: \"%s\"", c.getIdCategoria(), c.getNombre()))
                .collect(Collectors.joining("\n"));

        String base64Image = Base64.getEncoder().encodeToString(archivo.getBytes());
        String mediaType = archivo.getContentType() != null ? archivo.getContentType() : "image/jpeg";

        String prompt = construirPrompt(listaCategorias);
        String requestBody = construirRequestBody(base64Image, mediaType, prompt);

        String url = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                model, apiKey
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error en la API de Gemini: " + response.statusCode() + " - " + response.body());
        }

        return parsearRespuesta(response.body(), categorias);
    }

    private String construirPrompt(String listaCategorias) {
        return """
                Eres un asistente especializado en analizar imagenes de tickets, recibos y facturas.

                Analiza la imagen proporcionada y extrae los siguientes datos:

                1. **descripcion**: Nombre del comercio o establecimiento. Si no es claro, describe brevemente el concepto del gasto.
                2. **importeOriginal**: El importe TOTAL de la transaccion (numero decimal, sin simbolo de moneda).
                3. **monedaOriginal**: Codigo ISO de la moneda (EUR, USD, GBP, JPY, MXN). Si ves el simbolo € es EUR, $ podria ser USD o MXN (usa el contexto).
                4. **fechaGasto**: Fecha de la transaccion en formato YYYY-MM-DD.
                5. **categoriaSugerida**: Nombre de la categoria mas apropiada de la lista siguiente.
                6. **idCategoriaSugerida**: ID numerico de la categoria elegida.

                Categorias disponibles en el sistema:
                %s

                REGLAS IMPORTANTES:
                - Responde UNICAMENTE con un JSON valido, sin texto adicional, sin markdown, sin ```json.
                - Si no puedes extraer un campo con confianza, pon null como valor.
                - NO inventes datos. Si no se ve claro, pon null.
                - El campo "confianza" es un numero entre 0 y 1 que indica tu confianza GLOBAL.
                - El campo "confianzaPorCampo" tiene la confianza individual de cada campo.

                Formato de respuesta (JSON puro):
                {
                  "descripcion": "string o null",
                  "importeOriginal": numero o null,
                  "monedaOriginal": "string o null",
                  "fechaGasto": "YYYY-MM-DD o null",
                  "categoriaSugerida": "string o null",
                  "idCategoriaSugerida": numero o null,
                  "confianza": 0.0 a 1.0,
                  "confianzaPorCampo": {
                    "descripcion": 0.0 a 1.0,
                    "importe": 0.0 a 1.0,
                    "moneda": 0.0 a 1.0,
                    "fecha": 0.0 a 1.0,
                    "categoria": 0.0 a 1.0
                  }
                }
                """.formatted(listaCategorias);
    }

    /**
     * Construye el JSON del request body para la API de Gemini.
     * Formato: https://ai.google.dev/api/generate-content
     */
    private String construirRequestBody(String base64Image, String mediaType, String prompt) throws IOException {
        // Gemini usa un formato diferente a Anthropic
        Map<String, Object> inlineData = new LinkedHashMap<>();
        inlineData.put("mimeType", mediaType);
        inlineData.put("data", base64Image);

        Map<String, Object> imagePart = new LinkedHashMap<>();
        imagePart.put("inlineData", inlineData);

        Map<String, Object> textPart = new LinkedHashMap<>();
        textPart.put("text", prompt);

        Map<String, Object> content = new LinkedHashMap<>();
        content.put("parts", List.of(imagePart, textPart));

        Map<String, Object> generationConfig = new LinkedHashMap<>();
        generationConfig.put("maxOutputTokens", maxTokens);
        generationConfig.put("temperature", 0.1);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("contents", List.of(content));
        body.put("generationConfig", generationConfig);

        return objectMapper.writeValueAsString(body);
    }

    /**
     * Parsea la respuesta JSON de la API de Gemini y la convierte en AnalisisTicketDTO.
     */
    private AnalisisTicketDTO parsearRespuesta(String responseBody, List<Categoria> categorias) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);

        // Gemini: candidates[0].content.parts[0].text
        JsonNode candidates = root.get("candidates");
        if (candidates == null || !candidates.isArray() || candidates.isEmpty()) {
            throw new RuntimeException("Respuesta de Gemini sin candidatos");
        }

        JsonNode parts = candidates.get(0).get("content").get("parts");
        if (parts == null || !parts.isArray() || parts.isEmpty()) {
            throw new RuntimeException("Respuesta de Gemini sin contenido");
        }

        String textoRespuesta = parts.get(0).get("text").asText();

        // Limpiar posibles artefactos (```json, etc.)
        textoRespuesta = textoRespuesta.trim();
        if (textoRespuesta.startsWith("```")) {
            textoRespuesta = textoRespuesta.replaceAll("```json\\s*", "").replaceAll("```\\s*", "");
        }

        JsonNode data = objectMapper.readTree(textoRespuesta);

        AnalisisTicketDTO dto = new AnalisisTicketDTO();

        if (data.has("descripcion") && !data.get("descripcion").isNull()) {
            dto.setDescripcion(data.get("descripcion").asText());
        }

        if (data.has("importeOriginal") && !data.get("importeOriginal").isNull()) {
            dto.setImporteOriginal(new BigDecimal(data.get("importeOriginal").asText()));
        }

        if (data.has("monedaOriginal") && !data.get("monedaOriginal").isNull()) {
            dto.setMonedaOriginal(data.get("monedaOriginal").asText());
        }

        if (data.has("fechaGasto") && !data.get("fechaGasto").isNull()) {
            dto.setFechaGasto(data.get("fechaGasto").asText());
        }

        if (data.has("categoriaSugerida") && !data.get("categoriaSugerida").isNull()) {
            dto.setCategoriaSugerida(data.get("categoriaSugerida").asText());
        }
        if (data.has("idCategoriaSugerida") && !data.get("idCategoriaSugerida").isNull()) {
            dto.setIdCategoriaSugerida(data.get("idCategoriaSugerida").asLong());
        }

        if (data.has("confianza")) {
            dto.setConfianza(new BigDecimal(data.get("confianza").asText()));
        }

        if (data.has("confianzaPorCampo")) {
            JsonNode campos = data.get("confianzaPorCampo");
            Map<String, BigDecimal> confianzaMap = new LinkedHashMap<>();
            campos.fieldNames().forEachRemaining(field ->
                    confianzaMap.put(field, new BigDecimal(campos.get(field).asText()))
            );
            dto.setConfianzaPorCampo(confianzaMap);
        }

        return dto;
    }
}
