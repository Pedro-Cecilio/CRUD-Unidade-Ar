package com.dbserver.crud.domain.pessoa;

import java.time.LocalDate;

import com.dbserver.crud.domain.endereco.Endereco;
import com.dbserver.crud.domain.pessoa.dto.AtualizarDadosPessoaDto;
import com.dbserver.crud.domain.pessoa.dto.CriarPessoaDto;
import com.dbserver.crud.utils.Utils;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;
import static com.dbserver.crud.utils.Utils.validarRegex;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Entity
@Table(name = "pessoas")
@Getter
public class Pessoa implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    @Size(min = 6, message = "Senha deve conter no mínimo 6 caracteres.")
    private String senha;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Column(unique = true, nullable = false)
    @Size(min = 11, max = 11, message = "Cpf deve conter 11 caracteres numéricos")
    private String cpf;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "pessoa")
    @JsonManagedReference
    private List<Endereco> endereco = new ArrayList<>();

    protected Pessoa() {

    }

    public Pessoa(Long id, String login, String senha, String nome, LocalDate dataNascimento, String cpf,
            PasswordEncoder passwordEnconder) {
        this.id = id;
        setLogin(login);
        setSenha(senha, passwordEnconder);
        setNome(nome);
        setDataNascimento(dataNascimento);
        setCpf(cpf);
    }

    public Pessoa(CriarPessoaDto novaPessoa, PasswordEncoder passwordEnconder) {
        setLogin(novaPessoa.login());
        setSenha(novaPessoa.senha(), passwordEnconder);
        setNome(novaPessoa.nome());
        setDataNascimento(novaPessoa.dataNascimento());
        setCpf(novaPessoa.cpf());
    }

    public void atualizarDados(AtualizarDadosPessoaDto dados, PasswordEncoder passwordEnconder) {
        setSenha(dados.senha() != null && !dados.senha().isEmpty() ? dados.senha() : this.senha, passwordEnconder);
        setDataNascimento(dados.dataNascimento() != null ? dados.dataNascimento() : this.dataNascimento);
        setCpf(dados.cpf() != null && !dados.cpf().isEmpty() ? dados.cpf() : this.cpf);
        setNome(dados.nome() != null && !dados.nome().isEmpty() ? dados.nome() : this.nome);
    }

    public void setEndereco(Endereco endereco) {
        if (endereco == null) {
            return;
        }
        this.endereco.add(endereco);
    }

    public void setLogin(String login) {
        if (login == null || login.trim().isEmpty())
            throw new IllegalArgumentException("Login deve ser informado");
        if (login.length() < 5)
            throw new IllegalArgumentException("Login deve ter no mínimo 5 caracteres");
        this.login = login;
    }

    public void setSenha(String senha, PasswordEncoder passwordEnconder) {
        if (senha == null || senha.length() < 6) {
            throw new IllegalArgumentException("Senha deve conter no mínimo 6 caracteres");
        }
        this.senha = passwordEnconder.encode(senha);
    }

    public void setDataNascimento(LocalDate data) {
        if (data == null) {
            throw new IllegalArgumentException("Data de nascimento deve ser informada");
        }
        this.dataNascimento = data;
    }

    public void setNome(String nome) {
        if (nome == null)
            throw new IllegalArgumentException("Nome deve ser informada");
        if (nome.length() < 3)
            throw new IllegalArgumentException("Nome deve ter no mínimo 3 caracteres");
        this.nome = nome.trim();
    }

    public void setCpf(String cpf) {
        if (cpf == null)
            throw new IllegalArgumentException("Cpf deve ser informado");
        if (!validarRegex(Utils.REGEX_CPF, cpf))
            throw new IllegalArgumentException("Cpf deve conter 11 caracteres numéricos");
        this.cpf = cpf;
    }

    public boolean compararSenha(String senha, PasswordEncoder passwordEnconder) {
        return passwordEnconder.matches(senha, this.getSenha());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
