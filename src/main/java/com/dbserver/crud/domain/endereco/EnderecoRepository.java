package com.dbserver.crud.domain.endereco;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    Optional<Endereco> findByPessoaIdAndPrincipal(Long pessoaId, boolean principal);
    Optional<Endereco> findByIdAndPessoaId(Long id, Long pessoaId);
    Page<Endereco> findAllByPessoaId(Pageable pageable, Long pessoaId);
}
