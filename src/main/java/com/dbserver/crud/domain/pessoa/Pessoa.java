package com.dbserver.crud.domain.pessoa;

import java.time.LocalDate;

import com.dbserver.crud.domain.endereco.Endereco;
import com.dbserver.crud.domain.pessoa.dto.CriarPessoaDto;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "pessoas")
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Column(unique = true, nullable = false)
    private String cpf;

    @OneToMany
    private List<Endereco> endereco;
    
    protected Pessoa(){

    }
    
    public Pessoa(String nome, LocalDate dataNascimento, String cpf) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.cpf = cpf;
    }
    public Pessoa(CriarPessoaDto novaPessoa) {
        this.nome = novaPessoa.nome();
        this.dataNascimento = novaPessoa.dataNascimento();
        this.cpf = novaPessoa.cpf();
    }
}
