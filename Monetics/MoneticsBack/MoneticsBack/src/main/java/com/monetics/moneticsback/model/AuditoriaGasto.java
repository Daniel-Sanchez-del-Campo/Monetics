package com.monetics.moneticsback.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_gastos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaGasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Long idAuditoria;

    @Column(name = "estado_anterior", length = 30)
    private String estadoAnterior;

    @Column(name = "estado_nuevo", nullable = false, length = 30)
    private String estadoNuevo;

    @Column(name = "fecha_cambio")
    private LocalDateTime fechaCambio;

    @Column(length = 255)
    private String comentario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gasto", nullable = false)
    private Gasto gasto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_accion", nullable = false)
    private Usuario usuarioAccion;
}
