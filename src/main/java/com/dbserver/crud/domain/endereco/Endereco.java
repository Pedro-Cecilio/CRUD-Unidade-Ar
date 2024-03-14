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

    public Endereco(Long id, String rua, String numero, String bairro, String cidade, String estado, String cep,
            Boolean principal, Pessoa pessoa) {
        this.id = id;
        setRua(rua);
        setNumero(numero);
        setBairro(bairro);
        setCidade(cidade);
        setEstado(estado);
        setCep(cep);
        setPrincipal(principal);
        setPessoa(pessoa);
    }

    public Endereco(CriarEnderecoDto endereco) {
        setRua(endereco.rua());
        setNumero(endereco.numero());
        setBairro(endereco.bairro());
        setCidade(endereco.cidade());
        setEstado(endereco.estado());
        setCep(endereco.cep());
    }

    public void atualizarDados(AtualizarEnderecoDto dados) {
        setRua(dados.rua());
        setNumero(dados.numero());
        setBairro(dados.bairro());
        setCidade(dados.cidade());
        setEstado(dados.estado());
        setCep(dados.cep());
    }

    public void setPrincipal(Boolean principal) {
        if (principal != null) {
            this.principal = principal;
        }
    }

    public void setPessoa(Pessoa pessoa) {
        if (pessoa == null) {
            throw new IllegalArgumentException("Pessoa não pode ser nula");
        }
        this.pessoa = pessoa;
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
        this.cep = cep;
    }

    public void setRua(String rua) {
        if (rua == null || rua.trim().isEmpty()) {
            throw new IllegalArgumentException("Rua deve ser informada");
        }
        this.rua = rua;
    }

    public void setBairro(String bairro) {
        if (bairro == null || bairro.trim().isEmpty()) {
            throw new IllegalArgumentException("Bairro deve ser informado");
        }
        this.bairro = bairro;
    }

    public void setCidade(String cidade) {
        if (cidade == null || cidade.trim().isEmpty()) {
            throw new IllegalArgumentException("Cidade deve ser informada");
        }
        this.cidade = cidade;
    }

    public void setEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("Estado deve ser informado");
        }
        this.estado = estado;
    }

}
