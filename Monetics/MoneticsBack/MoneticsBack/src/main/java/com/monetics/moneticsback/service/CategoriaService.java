package com.monetics.moneticsback.service;

import com.monetics.moneticsback.dto.CategoriaDTO;
import com.monetics.moneticsback.dto.CrearCategoriaDTO;
import com.monetics.moneticsback.exception.OperacionNoPermitidaException;
import com.monetics.moneticsback.exception.RecursoNoEncontradoException;
import com.monetics.moneticsback.model.Categoria;
import com.monetics.moneticsback.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaDTO> obtenerActivas() {
        return categoriaRepository.findByActivaTrue().stream()
                .map(this::mapearACategoriaDTO)
                .collect(Collectors.toList());
    }

    public List<CategoriaDTO> obtenerTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::mapearACategoriaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoriaDTO crearCategoria(CrearCategoriaDTO dto) {
        categoriaRepository.findByNombre(dto.getNombre()).ifPresent(c -> {
            throw new OperacionNoPermitidaException("Ya existe una categoría con ese nombre");
        });

        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        categoria.setColor(dto.getColor() != null ? dto.getColor() : "#64b5f6");
        categoria.setActiva(true);

        return mapearACategoriaDTO(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaDTO actualizarCategoria(Long idCategoria, CrearCategoriaDTO dto) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));

        categoriaRepository.findByNombre(dto.getNombre()).ifPresent(c -> {
            if (!c.getIdCategoria().equals(idCategoria)) {
                throw new OperacionNoPermitidaException("Ya existe una categoría con ese nombre");
            }
        });

        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        if (dto.getColor() != null) {
            categoria.setColor(dto.getColor());
        }

        return mapearACategoriaDTO(categoriaRepository.save(categoria));
    }

    @Transactional
    public void desactivarCategoria(Long idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));
        categoria.setActiva(false);
        categoriaRepository.save(categoria);
    }

    private CategoriaDTO mapearACategoriaDTO(Categoria c) {
        return new CategoriaDTO(
                c.getIdCategoria(),
                c.getNombre(),
                c.getDescripcion(),
                c.getColor(),
                c.getActiva()
        );
    }
}
