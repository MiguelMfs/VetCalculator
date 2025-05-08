package com.calculadora.veterinaria.backend.repository;
import java.util.Optional;
import com.calculadora.veterinaria.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByEmail(String email);
    Optional<Usuario> findByEmail(String email);

}
