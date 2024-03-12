package com.dbserver.crud.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.dbserver.crud.domain.pessoa.Pessoa;


@Service
public class TokenService {
    
    @Value("${api.security.token.senha}")
    private String senha;

    public String gerarToken(Pessoa pessoa) {

        try {
            Algorithm algorithm = Algorithm.HMAC256(senha);
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
            Algorithm algorithm = Algorithm.HMAC256(senha);
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