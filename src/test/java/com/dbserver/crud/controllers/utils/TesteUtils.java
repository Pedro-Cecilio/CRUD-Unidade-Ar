package com.dbserver.crud.controllers.utils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.dbserver.crud.domain.pessoa.Pessoa;

public class TesteUtils {
    public static void login(Pessoa pessoa) {
        var auth = new UsernamePasswordAuthenticationToken(pessoa, null,
                pessoa.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
