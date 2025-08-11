package com.chatoapi.apirest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    private boolean looksLikeJwt(String token) {
        // Un JWT compact doit contenir exactement 2 points : header.payload.signature
        return StringUtils.hasText(token) && token.chars().filter(c -> c == '.').count() == 2;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, java.io.IOException {

        // 1) Si pas d’Authorization Bearer -> on laisse passer (route publique ou 401
        // plus loin)
        String header = req.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        // 2) Récupérer le token (après "Bearer ")
        String token = header.substring(7).trim();

        // 3) Si ce n’est pas un JWT compact → on ignore poliment
        if (!looksLikeJwt(token)) {
            SecurityContextHolder.clearContext();
            chain.doFilter(req, res);
            return;
        }

        // 4) Tenter l’authentification
        try {
            String email = jwtService.extractUsername(token);
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userDetailsService.loadUserByUsername(email);
                if (jwtService.validateToken(token)) {
                    var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (UsernameNotFoundException | JwtException | IllegalArgumentException e) {
            // Token invalide / parsing impossible → on nettoie et on continue (évite un
            // 500)
            SecurityContextHolder.clearContext();
        }

        // 5) Poursuivre la chaîne
        chain.doFilter(req, res);
    }
}
