package com.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.blog.authenticationProvider.JWTAuthenticationProvider;
import com.blog.security.filters.JWTAuthenticationFilter;
import com.blog.security.filters.JWTRefreshFilter;
import com.blog.security.filters.JwtValidationFilter;
import com.blog.utils.JWTUtil;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfigForJWT {

	private JWTUtil jwtUtil;
	private UserDetailsService userDetailsService;

	@Autowired
	public SecurityConfigForJWT(JWTUtil jwtUtil, UserDetailsService userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	@Bean
	public JWTAuthenticationProvider jwtAuthenticationProvider() {
		return new JWTAuthenticationProvider(jwtUtil, userDetailsService);
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {

		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager,
			JWTUtil jwtUtil) throws Exception {

		// Authentication filter responsible for login
		JWTAuthenticationFilter jwtAuthFilter = new JWTAuthenticationFilter(authenticationManager, jwtUtil);

		// Validation filter for checking JWT in every request
		 JwtValidationFilter jwtValidationFilter = new
		 JwtValidationFilter(authenticationManager);

		// refresh filter for checking JWT in every request
		 JWTRefreshFilter jwtRefreshFilter = new JWTRefreshFilter(jwtUtil,
		 authenticationManager);

		http.authorizeHttpRequests(auth -> auth

				// 🔓 PUBLIC APIs
				.requestMatchers("/api/users/", "/api/categories/", "/api/posts/", "/api/post/**", "/api/user/**",
						"/api/category/**", "/api/comments/**", "/api/post/image/**")
				.permitAll()

				// 🔐 ADMIN ONLY
				.requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")

				// 🔐 USER & ADMIN
				.requestMatchers(HttpMethod.POST, "/api/user/**", "/api/post/**").hasAnyRole("USER", "ADMIN")

				// 🔒 EVERYTHING ELSE
				.anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.csrf(csrf -> csrf.disable())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // generate token filter
		 .addFilterAfter(jwtValidationFilter, JWTAuthenticationFilter.class) //
		// validate token filter
		 .addFilterAfter(jwtRefreshFilter, JwtValidationFilter.class); // refresh
		// token filter
		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(DaoAuthenticationProvider daoAuthenticationProvider,
			JWTAuthenticationProvider jwtAuthenticationProvider) {

		return new ProviderManager(daoAuthenticationProvider, jwtAuthenticationProvider);
	}

}
