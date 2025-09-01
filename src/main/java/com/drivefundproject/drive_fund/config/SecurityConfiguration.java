package com.drivefundproject.drive_fund.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//     http
//         .csrf()
//         .disable()
//         // .csrf(csrf -> csrf
//         //     .ignoringRequestMatchers("/api/v1/auth/image/add-car")
//         // )
//         .authorizeHttpRequests()
//         .requestMatchers("/api/v1/auth/login", "/api/v1/auth/register","/api/v1/auth/image/add-car") // Only permit paths for registration and authentication
//         .permitAll()
//         .anyRequest()
//         .authenticated()
//         .and()
//         .sessionManagement()
//         .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//         .and()
//         .authenticationProvider(authenticationProvider)
//         .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

//     return http.build();
// }
       public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http
        // 1. New, functional way to disable CSRF
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            // 2. Clearer way to define public and protected endpoints
            .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login")
            .permitAll()
            .anyRequest()
            .authenticated()
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
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Allows all endpoints
                        .allowedOrigins("http://localhost:4200") // Front-end URL
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*");
            }
        };
    }
}
