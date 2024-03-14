package com.dbserver.crud.infra.security;

import java.io.IOException;
import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.dbserver.crud.domain.pessoa.Pessoa;
import com.dbserver.crud.domain.pessoa.PessoaRepository;
import com.dbserver.crud.infra.excecao.RespostaErro;
import com.dbserver.crud.infra.excecao.novasExcecoes.ValidarJwtExeption;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private TokenService tokenService;
    private PessoaRepository pessoaRepository;

    public SecurityFilter(TokenService tokenService, PessoaRepository pessoaRepository) {
        this.tokenService = tokenService;
        this.pessoaRepository = pessoaRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var token = this.recuperarToken(request);
        
        if (token != null) {
            try {
                String login = tokenService.validarToken(token);

                Optional<Pessoa> user = pessoaRepository.findByLogin(login);
                if (user.isPresent()) {
                    var authentication = new UsernamePasswordAuthenticationToken(user.get(), null,
                            user.get().getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    this.respostaErro(response);
                    return;
                }
            } catch (ValidarJwtExeption e) {
                this.respostaErro(response);
                return;
            }
        }
        filterChain.doFilter(request, response);

    }

    private String recuperarToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null)
            return null;
        return authHeader.replace("Bearer ", "");
    }

    public void respostaErro(HttpServletResponse response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        RespostaErro responseError = new RespostaErro("Token inválido");
        String tokenErrorResponse = mapper.writeValueAsString(responseError);
        
        response.setStatus(401);
        response.addHeader("Content-type", "application/json");
        response.getWriter().write(tokenErrorResponse);
    }
}