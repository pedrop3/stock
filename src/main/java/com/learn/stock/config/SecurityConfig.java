//package com.learn.stock.config;
//
//import com.learn.stock.security.ClientIdAuthFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http, ClientIdAuthFilter clientIdAuthFilter) throws Exception {
//        http.csrf().disable()
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(clientIdAuthFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}
