package com.calculadora.veterinaria.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

@Service
public class TokenService {

    @Value("${jwt.secret:senha-super-secreta}")
    private String secretKey;

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 2; // 2 horas

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String gerarToken(String email) {
        try {
            String headerJson = objectMapper.writeValueAsString(
                    objectMapper.createObjectNode()
                            .put("alg", "HS512")
                            .put("typ", "JWT")
            );

            String payloadJson = objectMapper.writeValueAsString(
                    objectMapper.createObjectNode()
                            .put("sub", email)
                            .put("exp", System.currentTimeMillis() + EXPIRATION_TIME)
            );

            String header = Base64.getUrlEncoder().withoutPadding().encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
            String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            String data = header + "." + payload;

            String signature = gerarAssinatura(data);
            return data + "." + signature;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar token JWT", e);
        }
    }

    private String gerarAssinatura(String data) {
        try {
            Key secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(secretKeySpec);
            byte[] signatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar assinatura JWT", e);
        }
    }

    public boolean validarAssinatura(String token) {
        String[] partes = token.split("\\.");
        if (partes.length != 3) return false;
        String data = partes[0] + "." + partes[1];
        String signatureCalculada = gerarAssinatura(data);
        return signatureCalculada.equals(partes[2]);
    }

    public String getEmailFromToken(String token) {
        try {
            if (!validarAssinatura(token)) {
                throw new RuntimeException("Assinatura inválida");
            }

            String[] partes = token.split("\\.");
            String payloadDecoded = new String(Base64.getUrlDecoder().decode(partes[1]), StandardCharsets.UTF_8);

            JsonNode node = objectMapper.readTree(payloadDecoded);
            long exp = node.get("exp").asLong();
            if (System.currentTimeMillis() > exp) {
                throw new RuntimeException("Token expirado");
            }

            return node.get("sub").asText();

        } catch (Exception e) {
            throw new RuntimeException("Token inválido ou malformado", e);
        }
    }
}
