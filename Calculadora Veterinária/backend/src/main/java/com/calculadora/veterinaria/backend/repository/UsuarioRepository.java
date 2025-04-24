package com.calculadora.veterinaria.backend.repository;

import com.calculadora.veterinaria.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByEmail(String email);
    Usuario findByEmail(String email);
}
