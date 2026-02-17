package com.monetics.moneticsback.repository.specification;

import com.monetics.moneticsback.model.Gasto;
import com.monetics.moneticsback.model.enums.EstadoGasto;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GastoSpecification {

    public static Specification<Gasto> conUsuario(Long idUsuario) {
        return (root, query, cb) -> idUsuario == null ? null :
                cb.equal(root.get("usuario").get("idUsuario"), idUsuario);
    }

    public static Specification<Gasto> conEstado(EstadoGasto estado) {
        return (root, query, cb) -> estado == null ? null :
                cb.equal(root.get("estadoGasto"), estado);
    }

    public static Specification<Gasto> conDepartamento(Long idDepartamento) {
        return (root, query, cb) -> idDepartamento == null ? null :
                cb.equal(root.get("departamento").get("idDepartamento"), idDepartamento);
    }

    public static Specification<Gasto> conCategoria(Long idCategoria) {
        return (root, query, cb) -> idCategoria == null ? null :
                cb.equal(root.get("categoria").get("idCategoria"), idCategoria);
    }

    public static Specification<Gasto> conFechaDesde(LocalDate desde) {
        return (root, query, cb) -> desde == null ? null :
                cb.greaterThanOrEqualTo(root.get("fechaGasto"), desde);
    }

    public static Specification<Gasto> conFechaHasta(LocalDate hasta) {
        return (root, query, cb) -> hasta == null ? null :
                cb.lessThanOrEqualTo(root.get("fechaGasto"), hasta);
    }

    public static Specification<Gasto> conImporteMinimo(BigDecimal min) {
        return (root, query, cb) -> min == null ? null :
                cb.greaterThanOrEqualTo(root.get("importeEur"), min);
    }

    public static Specification<Gasto> conImporteMaximo(BigDecimal max) {
        return (root, query, cb) -> max == null ? null :
                cb.lessThanOrEqualTo(root.get("importeEur"), max);
    }

    public static Specification<Gasto> conTexto(String texto) {
        return (root, query, cb) -> (texto == null || texto.isBlank()) ? null :
                cb.like(cb.lower(root.get("descripcion")), "%" + texto.toLowerCase() + "%");
    }

    public static Specification<Gasto> delEquipo(Long idManager) {
        return (root, query, cb) -> {
            if (idManager == null) return null;
            Join<?, ?> usuario = root.join("usuario");
            return cb.equal(usuario.get("manager").get("idUsuario"), idManager);
        };
    }
}
