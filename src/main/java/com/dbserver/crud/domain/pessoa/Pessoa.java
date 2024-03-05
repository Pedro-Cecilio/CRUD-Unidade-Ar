package com.dbserver.crud.domain.pessoa;

import java.time.LocalDate;

import com.dbserver.crud.domain.pessoa.dto.CriarPessoaDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pessoas")
@Getter
@Setter
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Column(unique = true, nullable = false)
    private String cpf;

    public Pessoa(String nome, LocalDate dataNascimento, String cpf) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.cpf = cpf;
    }
    public Pessoa(CriarPessoaDTO novaPessoa) {
        this.nome = novaPessoa.nome();
        this.dataNascimento = novaPessoa.dataNascimento();
        this.cpf = novaPessoa.cpf();
    }
}
