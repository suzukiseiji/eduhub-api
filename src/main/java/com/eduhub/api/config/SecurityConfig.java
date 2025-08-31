package com.eduhub.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de Segurança - TEMPORÁRIA
 * 
 * Por enquanto vamos desabilitar para focar no MongoDB
 * Depois implementaremos JWT completo
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Desabilitar CSRF (necessário para APIs REST)
            .csrf(csrf -> csrf.disable())
            
            // Permitir TODAS as requisições sem autenticação
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            
            // Desabilitar form login
            .formLogin(form -> form.disable())
            
            // Desabilitar http basic
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}