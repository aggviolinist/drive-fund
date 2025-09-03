package com.drivefundproject.drive_fund.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.security.config.Customizer.withDefaults; // Add this line

import java.util.Arrays;
import java.util.Collections;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;    
    private final Filter jwtAuthFilter;

     public SecurityConfiguration(AuthenticationProvider authenticationProvider, 
             @Qualifier("jwtAuthenticationFilter") Filter jwtAuthFilter) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
       public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http
        // 1. New, functional way to disable CSRF
        .cors(withDefaults()) 
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            // 2. Clearer way to define public and protected endpoints
            .requestMatchers(
        // "/swagger-ui.html", 
        // "/swagger-ui/**", 
        // "/v3/api-docs/**", 
        // "/api/v1/auth/**",
        // "/v3/api-docs.yaml",
        // "/swagger-resources/**", 
        // "/webjars/**"
        "/v3/api-docs","/api/v1/auth/**", "/api/v1/auth/register", "/api/v1/auth/login","/v2/api-docs",
			"/v3/api-docs/**", "/swagger-resources", "/swagger-resources/**", "/configuration/ui",
			"/configuration/security", "/swagger-ui/**", "/webjars/**", "/swagger-ui.html",
			"/api/test/**", "/authenticate"
        ).permitAll()
        .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()


            //.requestMatchers("/api/v1/auth/register", "/api/v1/auth/login", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()            
            .anyRequest().authenticated()
        )
        // 3. New, functional way to manage sessions
        //Every request should be authenticated. "Onceperrequestfilter"
        //Don't store session, remain stateless
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        // 4. Add out AuthFilter filter
        //Set everything first then set securitycontextholder then UsernamePasswordAuthenticationFilter
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
    @Bean
    public CorsFilter corsFilter() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    final CorsConfiguration config = new CorsConfiguration();
   // config.setAllowCredentials(true);
    // Don't do this in production, use a proper list  of allowed origins
    config.setAllowedOrigins(Collections.singletonList("https://10670fce360d.ngrok-free.app"));
    config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept","Authorization"));
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
}
}
