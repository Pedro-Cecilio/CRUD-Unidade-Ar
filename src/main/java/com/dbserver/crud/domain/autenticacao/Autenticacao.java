package com.dbserver.crud.domain.autenticacao;


import lombok.Getter;

@Getter
public class Autenticacao {
    private String login;
    private String senha;
    
    protected Autenticacao() {
    }

    public Autenticacao(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }
}
