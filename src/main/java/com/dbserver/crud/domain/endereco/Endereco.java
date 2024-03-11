package com.dbserver.crud.domain.endereco;

import static com.dbserver.crud.utils.Utils.validarRegex;
import com.dbserver.crud.domain.endereco.dto.AtualizarEnderecoDto;
import com.dbserver.crud.domain.endereco.dto.CriarEnderecoDto;
import com.dbserver.crud.domain.pessoa.Pessoa;
import com.dbserver.crud.utils.Utils;
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

    public void atualizarDados(AtualizarEnderecoDto dados) {
        this.rua = dados.rua() != null && !dados.rua().isEmpty() ? dados.rua() : this.rua;
        this.numero = dados.numero() != null && !dados.numero().isEmpty() ? dados.numero() : this.numero;
        this.bairro = dados.bairro() != null && !dados.bairro().isEmpty() ? dados.bairro() : this.bairro;
        this.cidade = dados.cidade() != null && !dados.cidade().isEmpty() ? dados.cidade() : this.cidade;
        this.estado = dados.estado() != null && !dados.estado().isEmpty() ? dados.estado() : this.estado;
        this.cep = dados.cep() != null && !dados.cep().isEmpty() ? dados.cep() : this.cep;
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

    public void setNumero(String numero) {
        if (numero == null)
            throw new IllegalArgumentException("Número deve ser informado");
        if (!validarRegex(Utils.REGEX_NUMERO_ENDERECO, numero))
            throw new IllegalArgumentException("Número deve ter somente caracteres numéricos");
        this.numero = numero;
    }

    public void setCep(String cep) {
        if (cep == null)
            throw new IllegalArgumentException("Cep deve ser informado");
        if (!validarRegex(Utils.REGEX_CEP, cep))
            throw new IllegalArgumentException("Número deve ter 8 caracteres numéricos");
    }

}
