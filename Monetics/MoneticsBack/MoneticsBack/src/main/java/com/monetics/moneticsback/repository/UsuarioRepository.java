package com.monetics.moneticsback.repository;

import com.monetics.moneticsback.model.Usuario;
import com.monetics.moneticsback.model.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository encargado de acceder a los datos de los usuarios.
 *
 * Se utiliza principalmente para:
 * - Autenticación (buscar usuario por email)
 * - Gestión de jerarquía (manager -> empleados)
 * - Filtrado por rol
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar un usuario por email (clave para login y JWT)
    Optional<Usuario> findByEmail(String email);

    // Obtener todos los usuarios de un departamento concreto
    List<Usuario> findByDepartamento_IdDepartamento(Long idDepartamento);

    // Obtener empleados de un manager concreto
    List<Usuario> findByManager_IdUsuario(Long idManager);

    // Obtener usuarios por rol (útil para administración)
    List<Usuario> findByRol(RolUsuario rol);
}
