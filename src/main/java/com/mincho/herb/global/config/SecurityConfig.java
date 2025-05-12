package com.mincho.herb.global.config;

import com.mincho.herb.infra.auth.CustomOauth2UserService;
import com.mincho.herb.infra.auth.JwtAuthFilter;
import com.mincho.herb.infra.auth.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomOauth2UserService customOauth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;


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
                            .requestMatchers("/oauth2/**", "/login/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/v1/community/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/v1/herbs/**").permitAll()
                            .requestMatchers("/api/v1/users/send-verification").permitAll()
                            .requestMatchers("/api/v1/users/send-verification-code").permitAll()
                            .requestMatchers("/api/v1/notification/**").permitAll()
                            .requestMatchers("/api/v1/users/**").hasRole("USER")
                            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // 관리자만 허용
                            .anyRequest().permitAll()
        );
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOauth2UserService))
                .successHandler(oAuth2SuccessHandler) // 소셜 로그인 성공 시 리디렉트 처리
        );


        return http.build();
    }


    // CORS 설정
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173","https://www.minchoherb.com", "https://api.minchoherb.com", "http://localhost:4174"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT","DELETE", "OPTIONS"));
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
