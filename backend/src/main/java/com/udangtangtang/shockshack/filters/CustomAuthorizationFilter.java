package com.udangtangtang.shockshack.filters;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.udangtangtang.shockshack.utils.JwtTokenVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenVerifier verifier;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/api/v1/auth/login")) {
            filterChain.doFilter(request, response);
        } else if (request.getServletPath().equals("/chat-websocket")) {
            String authorization = request.getParameter("Authorization");
            if (authorization != null && authorization.startsWith("Bearer ")) {
                String username = verifier.verifyAndGetUsername(getActualToken(authorization)).orElseThrow(IllegalStateException::new);
                setAuthentication(username);
                filterChain.doFilter(request, response);
            }
        } else {
            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorization != null && authorization.startsWith("Bearer ")) {
                try {
                    String token = getActualToken(authorization);
                    String username = verifier.verifyAndGetUsername(token).orElseThrow(IllegalStateException::new);
                    setAuthentication(username);
                    filterChain.doFilter(request, response);
                }catch (JWTVerificationException e) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }

    private String getActualToken(String tokenWithPrefix) {
        return tokenWithPrefix.substring("Bearer ".length());
    }

    private void setAuthentication(String username) {
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, "", authorities);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }
}
