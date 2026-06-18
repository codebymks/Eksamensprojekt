package org.example.exam.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletResponse;

//Assignment 5
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //Hashes the hardcoded passwords below instead of keeping them as plain text.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Declares two accounts: USER and ADMIN.
    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.withUsername("user")
                .password(passwordEncoder.encode("user123"))
                .roles("USER")
                .build();
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    //maps each endpoint to the role allowed to call it, and turns on HTTP login.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/sensor-data").permitAll()
                        .requestMatchers("/", "/index.html", "/user.html", "/html/**", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/api/login").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/sensor-readings").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/alerts/active").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/alerts").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/alerts/*/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/alerts/*/readings").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/alerts/*/reports").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/alerts/*/reports").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                //Returns a plain 401, so the browser's own login popup never appears - only index.html form is used.
                .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(
                        (request, response, authException) -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
                ));
        return http.build();
    }
}