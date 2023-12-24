package io.mfedirko.common.infra.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import static io.mfedirko.common.infra.security.AdminOAuthUserService.ADMIN_ROLE;

@Configuration
@Slf4j
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/admin/**").hasAuthority(ADMIN_ROLE)
                        .anyRequest().permitAll())
                .exceptionHandling(e -> e.authenticationEntryPoint(
                        new LoginUrlAuthenticationEntryPoint("/oauth-login"))
                        .accessDeniedHandler((request, response, ex) -> response.sendRedirect("/error")))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2Login(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public GitHubAuthorizer gitHubUserDecorator() {
        return new GitHubAuthorizer();
    }
}
