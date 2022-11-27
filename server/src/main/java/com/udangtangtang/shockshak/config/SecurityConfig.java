package com.udangtangtang.shockshak.config;

import com.udangtangtang.shockshak.filters.AuthenticationFilter;
import com.udangtangtang.shockshak.filters.AuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager(authenticationConfiguration));
        authenticationFilter.setFilterProcessesUrl("/api/v1/auth/login");
        authenticationFilter.setPostOnly(true);
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new AuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilter(authenticationFilter)
                .csrf().disable()
                .authorizeRequests().antMatchers("/api/v1/auth/**").permitAll()
                .anyRequest().authenticated();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
