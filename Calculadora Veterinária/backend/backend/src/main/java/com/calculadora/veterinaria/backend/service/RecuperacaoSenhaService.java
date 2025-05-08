package com.calculadora.veterinaria.backend.service;

import com.calculadora.veterinaria.backend.entity.PasswordResetToken;
import com.calculadora.veterinaria.backend.entity.Usuario;
import com.calculadora.veterinaria.backend.repository.PasswordResetTokenRepository;
import com.calculadora.veterinaria.backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecuperacaoSenhaService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public RecuperacaoSenhaService(UsuarioRepository usuarioRepository, PasswordResetTokenRepository tokenRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<String> gerarTokenParaEmail(String email) {
        return usuarioRepository.findByEmail(email).map(usuario -> {
            String token = UUID.randomUUID().toString();
            LocalDateTime expiracao = LocalDateTime.now().plusMinutes(30);

            PasswordResetToken tokenObj = new PasswordResetToken(token, usuario, expiracao);
            tokenRepository.save(tokenObj);
            return token;
        });
    }

    public boolean redefinirSenha(String token, String novaSenha) {
        return tokenRepository.findByToken(token).filter(t -> t.getExpiracao().isAfter(LocalDateTime.now()))
                .map(t -> {
                    Usuario usuario = t.getUsuario();
                    usuario.setSenha(passwordEncoder.encode(novaSenha));
                    usuarioRepository.save(usuario);
                    tokenRepository.delete(t);
                    return true;
                }).orElse(false);
    }
}
