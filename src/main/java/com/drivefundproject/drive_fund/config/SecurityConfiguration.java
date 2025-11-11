package com.drivefundproject.drive_fund.config;

// REMOVED: import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// Removed unused CORS imports

import static org.springframework.security.config.Customizer.withDefaults; 

import jakarta.servlet.Filter;
// REMOVED: import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

	private final AuthenticationProvider authenticationProvider;
	private final Filter jwtAuthFilter;

	// Constructor is correct for manual injection
	public SecurityConfiguration(
		AuthenticationProvider authenticationProvider, 
		@Qualifier("jwtAuthenticationFilter") Filter jwtAuthFilter
	) {
		this.authenticationProvider = authenticationProvider;
		this.jwtAuthFilter = jwtAuthFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	   http
		.csrf(csrf -> csrf.disable()) // Stateless, so CSRF is disabled 
		.authorizeHttpRequests(auth -> auth
			.requestMatchers(
				"/v3/api-docs", "/api/v1/auth/**", "/api/v1/auth/register", "/api/v1/auth/login", "/v2/api-docs",
				"/v3/api-docs/**", "/swagger-resources", "/swagger-resources/**", "/configuration/ui",
				"/configuration/security", "/swagger-ui/**", "/webjars/**", "/swagger-ui.html",
				"/api/test/**", "/authenticate", "/web/sockets/**", "/ws/**", "/favicon.ico", "/css/**", "/js/**", "/images/**"
			).permitAll()
			.requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
			.anyRequest().authenticated()
		)
		.sessionManagement(session -> session
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		)
		.authenticationProvider(authenticationProvider) // Added the AuthenticationProvider
		.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
// CORS Filter block is correctly commented out and removed for testing.