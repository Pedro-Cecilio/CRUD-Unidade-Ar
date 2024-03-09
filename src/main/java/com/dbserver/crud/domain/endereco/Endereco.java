package com.dbserver.crud.domain.endereco;

import com.dbserver.crud.domain.endereco.dto.CriarEnderecoDto;
import com.dbserver.crud.domain.pessoa.Pessoa;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "endereco")
@Getter
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String rua;

    @Column(nullable = false)
    private String numero;

    @Column(nullable = false)
    private String bairro;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private String cep;

    @Column(nullable = false)
    private Boolean principal = false;

    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    @JsonBackReference
    private Pessoa pessoa;

    protected Endereco() {
    }

    public Endereco(CriarEnderecoDto endereco) {
        this.rua = endereco.rua();
        this.numero = endereco.numero();
        this.bairro = endereco.bairro();
        this.cidade = endereco.cidade();
        this.estado = endereco.estado();
        this.cep = endereco.cep();
        setPrincipal(endereco.principal());
    }

    public void setPrincipal(Boolean principal) {
        if (principal != null) {
            this.principal = principal;
        }
    }

    public void setPessoa(Pessoa pessoa) {
        if (pessoa != null) {
            this.pessoa = pessoa;
        }
    }

}
