package com.phungquocthai.symphony.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	@Autowired
	private CustomJwtDecoder customJwtDecoder;

	private final String[] PUBLIC_GET_ENDPOINTS = {"/home/fruits", "/singer/**", "/song/**",
			"/auth/register", "/auth/login", "/static/**",
			"/ranking", "/newSongs", "/category", "/recentlyListen", "/favorite"};
	
	private final String[] PUBLIC_POST_ENDPOINTS = {"/auth/login", "/auth/register", "/auth/logout", "/auth/refresh"};
	
//	private final String[] ADMIN_ENDPOINTS = {"/user/users", "/singer/singers", "/singer/create"};
//	
//	private final String[] SINGER_ENDPOINTS = {"/song/create", "/song/delete"};
	
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request ->
        	request
//        		.requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
//        		.requestMatchers(SINGER_ENDPOINTS).hasAuthority("ROLE_SINGER")
        		.requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
        		.requestMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINTS).permitAll()
        		.anyRequest().authenticated());
        
        http.oauth2ResourceServer(oauth2 ->
        	oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder).jwtAuthenticationConverter(jwtAuthenticationConverter())));

        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
    
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
    	JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    	jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
    	
    	JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    	jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
    	return jwtAuthenticationConverter;
    }
  
}
