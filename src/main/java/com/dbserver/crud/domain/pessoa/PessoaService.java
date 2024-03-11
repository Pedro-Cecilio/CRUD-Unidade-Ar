package com.dbserver.crud.domain.pessoa;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.dbserver.crud.domain.endereco.Endereco;
import com.dbserver.crud.domain.endereco.EnderecoRepository;
import com.dbserver.crud.domain.endereco.dto.CriarEnderecoDto;
import com.dbserver.crud.domain.endereco.dto.EnderecoService;
import com.dbserver.crud.domain.pessoa.dto.AtualizarDadosPessoaDto;
import com.dbserver.crud.domain.pessoa.dto.CriarPessoaDto;
import com.dbserver.crud.domain.pessoa.dto.PessoaRespostaDto;

@Service
public class PessoaService {

    private PessoaRepository pessoaRepository;
    private PasswordEncoder passwordEnconder;
    private EnderecoRepository enderecoRepository;
    private EnderecoService enderecoService;

    public PessoaService(PessoaRepository pessoaRepository, PasswordEncoder passwordEnconder, EnderecoRepository enderecoRepository, EnderecoService enderecoService) {
        this.pessoaRepository = pessoaRepository;
        this.passwordEnconder = passwordEnconder;
        this.enderecoRepository = enderecoRepository;
        this.enderecoService = enderecoService;
    }

    public Pessoa salvarPessoa(CriarPessoaDto criarPessoaDTO) {
        Pessoa pessoa = new Pessoa(criarPessoaDTO, this.passwordEnconder);
        List<CriarEnderecoDto> enderecos = criarPessoaDTO.enderecos();
        this.pessoaRepository.save(pessoa);
        if(enderecos != null && !enderecos.isEmpty()) {
            enderecos.forEach(endereco ->{
                Endereco novoEndereco = new Endereco(endereco);
                novoEndereco.setPessoa(pessoa);
                if(novoEndereco.getPrincipal().equals(true)){
                    this.enderecoService.definirComoEnderecoPrincipal(pessoa.getId(), novoEndereco);
                }else{
                    this.enderecoRepository.save(novoEndereco);
                }
                pessoa.setEndereco(novoEndereco);
            });
        }
        this.pessoaRepository.save(pessoa);
        return pessoa;

    }

    public PessoaRespostaDto criarPessoaRespostaDto(Pessoa pessoa) {
        return new PessoaRespostaDto(pessoa);
    }

    public Pessoa atualizarDadosPessoa(AtualizarDadosPessoaDto atualizarDadosPessoaDto, Pessoa pessoa) {
        pessoa.atualizarDados(atualizarDadosPessoaDto, this.passwordEnconder);
        return this.pessoaRepository.save(pessoa);
    }

    public Pessoa pegarPessoaLogada() {
        Optional<Pessoa> pessoa = this.pessoaRepository.findById(this.pegarIdDaPessoaLogada());
        if (pessoa.isPresent()) {
            return pessoa.get();
        }
        throw new NoSuchElementException("Usuário não encontrado");
    }

    public Long pegarIdDaPessoaLogada() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Pessoa pessoaLogada = (Pessoa) authentication.getPrincipal();
        return  pessoaLogada.getId();
    }
}
