package com.monetics.moneticsback.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Servicio que consulta la API ExchangeRate-API para obtener
 * el tipo de cambio del día y convertir divisas a EUR.
 */
@Service
public class ExchangeRateService {

    private static final String API_URL = "https://open.er-api.com/v6/latest/";
    private final RestTemplate restTemplate;

    public ExchangeRateService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Obtiene el tipo de cambio de una moneda origen a EUR.
     * @param monedaOrigen código ISO (USD, GBP, JPY, MXN...)
     * @return tipo de cambio (cuántos EUR vale 1 unidad de monedaOrigen)
     */
    public BigDecimal obtenerTipoCambioAEur(String monedaOrigen) {
        if ("EUR".equalsIgnoreCase(monedaOrigen)) {
            return BigDecimal.ONE;
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(
                    API_URL + monedaOrigen,
                    Map.class
            );

            if (response != null && "success".equals(response.get("result"))) {
                @SuppressWarnings("unchecked")
                Map<String, Number> rates = (Map<String, Number>) response.get("rates");
                Number eurRate = rates.get("EUR");
                if (eurRate != null) {
                    return new BigDecimal(eurRate.toString()).setScale(6, RoundingMode.HALF_UP);
                }
            }
        } catch (Exception e) {
            // Si la API falla, log y usar tipo de cambio 1.0
            System.err.println("Error al obtener tipo de cambio para " + monedaOrigen + ": " + e.getMessage());
        }

        // Fallback: tipo de cambio 1.0
        return BigDecimal.ONE;
    }

    /**
     * Convierte un importe de moneda origen a EUR.
     */
    public BigDecimal convertirAEur(BigDecimal importeOriginal, String monedaOrigen) {
        BigDecimal tipoCambio = obtenerTipoCambioAEur(monedaOrigen);
        return importeOriginal.multiply(tipoCambio).setScale(2, RoundingMode.HALF_UP);
    }
}
