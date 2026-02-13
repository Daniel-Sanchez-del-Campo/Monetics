package com.monetics.moneticsback.model;

import com.monetics.moneticsback.model.enums.EstadoGasto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "gastos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_gasto")
    private Long idGasto;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(name = "importe_original", nullable = false)
    private BigDecimal importeOriginal;

    @Column(name = "moneda_original", nullable = false, length = 10)
    private String monedaOriginal;

    @Column(name = "importe_eur", nullable = false)
    private BigDecimal importeEur;

    @Column(name = "tipo_cambio", nullable = false)
    private BigDecimal tipoCambio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_gasto", nullable = false, length = 30)
    private EstadoGasto estadoGasto;

    @Column(name = "fecha_gasto", nullable = false)
    private LocalDate fechaGasto;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "imagen_ticket", columnDefinition = "LONGTEXT")
    private String imagenTicket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_departamento", nullable = false)
    private Departamento departamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @OneToMany(mappedBy = "gasto", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuditoriaGasto> auditorias;
}
