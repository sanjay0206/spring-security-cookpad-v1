package com.cookpad.security;

import com.cookpad.security.filters.JWTUsernamePasswordAuthFilter;
import com.cookpad.security.filters.JWTVerificationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JWTTokenProvider tokenProvider;
    private final AppConfig appConfig;
    private final ObjectMapper mapper;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          JWTTokenProvider tokenProvider,
                          AppConfig appConfig,
                          ObjectMapper mapper) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
        this.appConfig = appConfig;
        this.mapper = mapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .cors()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(getUsernamePasswordAuthFilter())
                .addFilterAfter(getJWTVerificationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                     .antMatchers(
                        "/v2/api-docs/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/swagger-ui/**",
                        "/webjars/**").permitAll()
                    .antMatchers(appConfig.getWhitelistedEndpoints()).permitAll()
                    .antMatchers(appConfig.getBlacklistedEndpoints()).denyAll()
                    .anyRequest().authenticated()
                .and()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(Collections.singletonList(authProvider));
    }

    @Bean
    public JWTUsernamePasswordAuthFilter getUsernamePasswordAuthFilter() {
        return new JWTUsernamePasswordAuthFilter(authenticationManager(), tokenProvider, appConfig, mapper);
    }

    @Bean
    public JWTVerificationFilter getJWTVerificationFilter() {
        return new JWTVerificationFilter(userDetailsService, tokenProvider, appConfig, mapper);
    }
}
