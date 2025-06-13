package com.ferdican.restaurantsystem.config;

import com.ferdican.restaurantsystem.services.CustomUserDetailservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    private final CustomUserDetailservice customUserDetailservice;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    public WebSecurityConfig(CustomUserDetailservice customUserDetailservice,
                             CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customUserDetailservice = customUserDetailservice;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    private final String[] publicUrl = {"/", "/global-search/**", "/register", "/register/**",
            "/webjars/**", "/resources/**", "/assets/**", "/css/**", "/summernote/**", "/js/**",
            "/*.css", "/*.js", "/*.js.map", "/fonts**", "/favicon.ico", "/resources/**", "/error",
            "/images/**"};

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());

        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(publicUrl).permitAll();
            auth.requestMatchers("/admin/**").hasAuthority("admin");
            auth.requestMatchers("/dashboard/manage").hasAuthority("admin");
            auth.requestMatchers("/kitchen/**").hasAuthority("kitchen staff");
            auth.requestMatchers("/waiter/**").hasAuthority("waiter");
            auth.requestMatchers("/orders/**").hasAnyAuthority("admin", "kitchen staff", "waiter");
            auth.requestMatchers("/schedule/**").hasAnyAuthority("admin", "waiter", "kitchen staff");
            auth.anyRequest().authenticated();
        });

        http.formLogin(form -> form.loginPage("/login").
                        permitAll().successHandler(customAuthenticationSuccessHandler))
                .logout(logout -> {
                    logout.logoutUrl("/logout");
                    logout.logoutSuccessUrl("/");
                }).cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(customUserDetailservice);
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
