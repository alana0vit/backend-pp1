package br.com.conectaPro.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Pular o filtro para as rotas de autenticação para evitar loops
        if (request.getServletPath().contains("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt;
        final String userEmail;
        
        // Localiza onde você verifica o authHeader
        final String authHeader = request.getHeader("Authorization");

        // Se for nulo ou não começar com Bearer, apenas passe para o próximo filtro
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // O return é CRUCIAL aqui para parar a execução deste filtro
        }

        // Extrai o token jwt
        jwt = authHeader.substring(7);

        // Extrai o email do user pelo token
        userEmail = jwtService.extractUsername(jwt);

        // Se temo o email do user e ele não está autenticado no contexto atual
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Carrega os dados do usuário
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Valida o token
            if (jwtService.isTokenValid(userEmail, userDetails)) {

                // Cria o token para autenticação com o Spring Security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userEmail, userDetails);

                // Adicionar os detalhes da requisição (IP, sessão, etc)
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                // Atualiza o contexto de segurança
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Passar a requisição adiante na cadeia de filtros
        filterChain.doFilter(request, response);
    }

}
