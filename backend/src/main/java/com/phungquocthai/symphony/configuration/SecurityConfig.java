package com.phungquocthai.symphony.configuration;

import java.util.Arrays;

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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	@Autowired
	private CustomJwtDecoder customJwtDecoder;

//	private final String[] PUBLIC_GET_ENDPOINTS = {"/", "/home", "/singer/**", "/song/**",
//			"/auth/register", "/auth/login", "/user/singer/**", "/user\\?id=\\d+",
//			"/music/**", "/lyric/**", "/lrc/**", "/images/**", "/singer/exclude",
//			"/ranking", "/newSongs", "/category", "/favorite", "/search/**", "/uploads/**"};
	
	private final String[] PUBLIC_GET_ENDPOINTS = {"/**"};
	
	private final String[] PUBLIC_POST_ENDPOINTS = {"/auth/login", "/auth/register", "/auth/logout",
			"/auth/refresh", "/song/listenedSong",
			"/singer/exclude**", "/singer/exclude/**", "/singer/exclude"};
	
//	private final String[] ADMIN_ENDPOINTS = {"/user/users", "/singer/singers", "/singer/create"};
//	
//	private final String[] SINGER_ENDPOINTS = {"/song/create", "/song/delete"};
	
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
    	
        http.authorizeHttpRequests(request ->
        	request
//        		.requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
//        		.requestMatchers(SINGER_ENDPOINTS).hasAuthority("ROLE_SINGER")
        		.requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
        		.requestMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINTS).permitAll()
        		.anyRequest().authenticated());
        
        http.oauth2ResourceServer(oauth2 ->
        	oauth2.jwt(jwtConfigurer -> jwtConfigurer
        			.decoder(customJwtDecoder)
        			.jwtAuthenticationConverter(jwtAuthenticationConverter())
        			)
        	.authenticationEntryPoint(customAuthenticationEntryPoint())
        	);

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
  
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
//        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            String path = request.getRequestURI();
            if (path.equals("/") || path.equals("/home")) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Public Page Accessible");
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            }
        };
    }
}
