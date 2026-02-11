package com.monetics.moneticsback.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardDTO {

    private List<GastoPorDepartamentoDTO> gastosPorDepartamento;
    private List<AlertaPresupuestoDTO> alertasPresupuesto;
    private BigDecimal totalPendienteReembolso;
    private BigDecimal totalAprobado;
    private BigDecimal totalGastosMes;
    private int totalGastos;
    private int gastosPendientes;
    private int gastosAprobados;
    private int gastosRechazados;

    // === Sub-DTOs ===

    public static class GastoPorDepartamentoDTO {
        private String departamento;
        private BigDecimal totalGastado;
        private BigDecimal presupuestoMensual;
        private int numGastos;

        public GastoPorDepartamentoDTO() {}

        public GastoPorDepartamentoDTO(String departamento, BigDecimal totalGastado, BigDecimal presupuestoMensual, int numGastos) {
            this.departamento = departamento;
            this.totalGastado = totalGastado;
            this.presupuestoMensual = presupuestoMensual;
            this.numGastos = numGastos;
        }

        public String getDepartamento() { return departamento; }
        public void setDepartamento(String departamento) { this.departamento = departamento; }
        public BigDecimal getTotalGastado() { return totalGastado; }
        public void setTotalGastado(BigDecimal totalGastado) { this.totalGastado = totalGastado; }
        public BigDecimal getPresupuestoMensual() { return presupuestoMensual; }
        public void setPresupuestoMensual(BigDecimal presupuestoMensual) { this.presupuestoMensual = presupuestoMensual; }
        public int getNumGastos() { return numGastos; }
        public void setNumGastos(int numGastos) { this.numGastos = numGastos; }
    }

    public static class AlertaPresupuestoDTO {
        private String departamento;
        private BigDecimal presupuestoMensual;
        private BigDecimal gastoActual;
        private double porcentajeUsado;

        public AlertaPresupuestoDTO() {}

        public AlertaPresupuestoDTO(String departamento, BigDecimal presupuestoMensual, BigDecimal gastoActual, double porcentajeUsado) {
            this.departamento = departamento;
            this.presupuestoMensual = presupuestoMensual;
            this.gastoActual = gastoActual;
            this.porcentajeUsado = porcentajeUsado;
        }

        public String getDepartamento() { return departamento; }
        public void setDepartamento(String departamento) { this.departamento = departamento; }
        public BigDecimal getPresupuestoMensual() { return presupuestoMensual; }
        public void setPresupuestoMensual(BigDecimal presupuestoMensual) { this.presupuestoMensual = presupuestoMensual; }
        public BigDecimal getGastoActual() { return gastoActual; }
        public void setGastoActual(BigDecimal gastoActual) { this.gastoActual = gastoActual; }
        public double getPorcentajeUsado() { return porcentajeUsado; }
        public void setPorcentajeUsado(double porcentajeUsado) { this.porcentajeUsado = porcentajeUsado; }
    }

    // === Getters/Setters del DTO principal ===

    public List<GastoPorDepartamentoDTO> getGastosPorDepartamento() { return gastosPorDepartamento; }
    public void setGastosPorDepartamento(List<GastoPorDepartamentoDTO> gastosPorDepartamento) { this.gastosPorDepartamento = gastosPorDepartamento; }
    public List<AlertaPresupuestoDTO> getAlertasPresupuesto() { return alertasPresupuesto; }
    public void setAlertasPresupuesto(List<AlertaPresupuestoDTO> alertasPresupuesto) { this.alertasPresupuesto = alertasPresupuesto; }
    public BigDecimal getTotalPendienteReembolso() { return totalPendienteReembolso; }
    public void setTotalPendienteReembolso(BigDecimal totalPendienteReembolso) { this.totalPendienteReembolso = totalPendienteReembolso; }
    public BigDecimal getTotalAprobado() { return totalAprobado; }
    public void setTotalAprobado(BigDecimal totalAprobado) { this.totalAprobado = totalAprobado; }
    public BigDecimal getTotalGastosMes() { return totalGastosMes; }
    public void setTotalGastosMes(BigDecimal totalGastosMes) { this.totalGastosMes = totalGastosMes; }
    public int getTotalGastos() { return totalGastos; }
    public void setTotalGastos(int totalGastos) { this.totalGastos = totalGastos; }
    public int getGastosPendientes() { return gastosPendientes; }
    public void setGastosPendientes(int gastosPendientes) { this.gastosPendientes = gastosPendientes; }
    public int getGastosAprobados() { return gastosAprobados; }
    public void setGastosAprobados(int gastosAprobados) { this.gastosAprobados = gastosAprobados; }
    public int getGastosRechazados() { return gastosRechazados; }
    public void setGastosRechazados(int gastosRechazados) { this.gastosRechazados = gastosRechazados; }
}
