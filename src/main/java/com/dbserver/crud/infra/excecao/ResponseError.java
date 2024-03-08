package com.dbserver.crud.infra.excecao;

public class ResponseError<T>{
    private T erro;
    public ResponseError(T erro){
        this.erro = erro;
    }
    public T getErro(){
        return this.erro;
    }
}
