package com.dbserver.crud.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.dbserver.crud.domain.pessoa.Pessoa;


@Service
public class TokenService {
    
    private String secret = "senha";

    public String gerarToken(Pessoa pessoa) {

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("CRUD Unidade Ar")
                    .withSubject(pessoa.getLogin())
                    .withClaim("userId", pessoa.getId())
                    .withExpiresAt(this.gerarDataExpiracao())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error while generating token: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Error Generico: " + e);
        }
    }

    public String validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("CRUD Unidade Ar")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTCreationException e) {
            return "";
        }
    }

    private Instant gerarDataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    

}