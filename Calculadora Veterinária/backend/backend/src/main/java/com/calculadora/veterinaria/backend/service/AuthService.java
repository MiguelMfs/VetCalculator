package com.calculadora.veterinaria.backend.service;

import com.calculadora.veterinaria.backend.entity.Usuario;
import com.calculadora.veterinaria.backend.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String email, String senha) throws Exception {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Usuário não encontrado"));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new Exception("Credenciais inválidas");
        }

        return generateToken(usuario);
    }

    private String generateToken(Usuario usuario) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
    
        return Jwts.builder()
                .setSubject(usuario.getEmail()) // Definindo o email do usuário como o 'subject' do token
                .setIssuedAt(now) // Data de criação do token
                .setExpiration(expiryDate) // Data de expiração do token
                .signWith(SignatureAlgorithm.HS512, jwtSecret) // Algoritmo e chave secreta para assinar o token
                .compact(); // Este método gera a string final do token JWT
    }
}
    
