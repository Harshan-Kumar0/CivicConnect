package org.civicconnect.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // ✅ Disable CSRF for API + form compatibility
                .csrf(csrf -> csrf.disable())

                // ✅ Prevent browsers from caching secured pages
                .headers(headers -> headers
                        .cacheControl(cache -> cache.disable())
                )

                // ✅ Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // 🌍 Publicly accessible routes for citizens
                        .requestMatchers(
                                "/",
                                "/index",
                                "/submit",
                                "/api/**",
                                "/track/**",
                                "/view/**",
                                "/css/**", "/js/**", "/images/**", "/webjars/**",
                                "/fragments/**",
                                "/favicon.ico", "/error"
                        ).permitAll()

                        // 🔒 Admin-only routes
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 🧩 Everything else safe fallback
                        .anyRequest().permitAll()
                )

                // ✅ Custom login configuration
                .formLogin(form -> form
                        .loginPage("/login")                  // Custom login page
                        .loginProcessingUrl("/perform_login") // Form submit endpoint
                        .defaultSuccessUrl("/admin", true)    // Redirect after successful login
                        .failureUrl("/login?error=true")      // On login failure
                        .permitAll()
                )

                // ✅ Logout configuration
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")                // Redirect to home after logout
                        .invalidateHttpSession(true)          // End session
                        .deleteCookies("JSESSIONID")          // Remove session cookie
                        .permitAll()
                )

                // ✅ Redirect unauthorized access attempts to login page
                .exceptionHandling(e -> e
                        .accessDeniedPage("/login")
                );

        return http.build();
    }

    // ✅ Use BCrypt for secure password hashing
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
