package com.mincho.herb.common.config;

import com.mincho.herb.infra.auth.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws  Exception{

        http.cors(cors ->cors.configurationSource(corsConfigurationSource()));
        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.logout(AbstractHttpConfigurer::disable);

        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        http.addFilterAt(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                    authorizationManagerRequestMatcherRegistry
                            .requestMatchers("/api/v1/users/register/**").permitAll()
                            .requestMatchers("/api/v1/users/login/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/v1/community/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/v1/herbs/**").permitAll()
                            .requestMatchers("/api/v1/users/send-verification").permitAll()
                            .requestMatchers("/api/v1/users/send-verification-code").permitAll()
                            .requestMatchers("/api/v1/users/**").hasAnyRole("USER")
                            .requestMatchers("/api/v1/users/**").hasAnyRole("USER")
                            .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자만 허용
                            .anyRequest().permitAll()
                );

        return http.build();
    }

    /*
    * http.authorizeHttpRequests(auth -> auth
    .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()  // GET 요청은 모두 허용
    .requestMatchers(HttpMethod.POST, "/api/posts/**").hasRole("ADMIN")  // POST 요청은 ADMIN만 허용
    .anyRequest().authenticated()
);
    *
    * */

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); // 허용할 헤더
        configuration.setAllowCredentials(true); // 서버 쿠키를 클라이언트에 설정할 수 있도록 허용
        configuration.addExposedHeader("Authorization"); // 클라이언트에 노출할 헤더 지정

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
