package com.monetics.moneticsback.model;

import com.monetics.moneticsback.model.enums.RolUsuario;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RolUsuario rol;

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_departamento", nullable = false)
    private Departamento departamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_manager")
    private Usuario manager;

    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    private List<Usuario> empleados;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Gasto> gastos;

    @OneToMany(mappedBy = "usuarioAccion", fetch = FetchType.LAZY)
    private List<AuditoriaGasto> auditorias;
}
