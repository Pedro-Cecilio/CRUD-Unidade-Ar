package com.dbserver.crud.infra.security;


import java.io.IOException;
import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.dbserver.crud.domain.pessoa.Pessoa;
import com.dbserver.crud.domain.pessoa.PessoaRepository;
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
            var login = tokenService.validarToken(token);
            Optional<Pessoa> user = pessoaRepository.findByLogin(login);

            if (user.isPresent()) {
                var authentication = new UsernamePasswordAuthenticationToken(user.get(), null, user.get().getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
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

}