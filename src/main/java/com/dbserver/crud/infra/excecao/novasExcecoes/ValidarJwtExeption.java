package com.dbserver.crud.infra.excecao.novasExcecoes;

public class ValidarJwtExeption extends RuntimeException{
    public ValidarJwtExeption() {
        super();
    }

    // Construtor com mensagem de erro
    public ValidarJwtExeption(String mensagem) {
        super(mensagem);
    }

    // Construtor com mensagem de erro e causa
    public ValidarJwtExeption(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
