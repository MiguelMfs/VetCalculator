package com.calculadora.veterinaria.backend.controller;

import com.calculadora.veterinaria.backend.service.RecuperacaoSenhaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/senha")
public class RecuperacaoSenhaController {

    private final RecuperacaoSenhaService service;

    public RecuperacaoSenhaController(RecuperacaoSenhaService service) {
        this.service = service;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> solicitarReset(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        return service.gerarTokenParaEmail(email)
                .map(token -> ResponseEntity.ok(Map.of("token", token)))
                .orElse(ResponseEntity.notFound().build());
    }


    public ResponseEntity<String> redefinirSenha(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String novaSenha = body.get("novaSenha");
        boolean sucesso = service.redefinirSenha(token, novaSenha);
    
        if (sucesso) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Token inválido ou expirado.");
        }
    }
}
    
