package io.mfedirko.common.infra.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint

@Configuration
class WebSecurityConfig {
    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests {
                it.requestMatchers("/admin/**").hasAuthority(AdminOAuthUserService.ADMIN_ROLE)
                    .anyRequest().permitAll()
            }
            .exceptionHandling {
                it.authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/oauth-login"))
                  .accessDeniedHandler { _: HttpServletRequest?, response: HttpServletResponse, _: AccessDeniedException? ->
                    response.sendRedirect("/error")
                  }
            }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .oauth2Login(Customizer.withDefaults())
        return http.build()
    }

    @Bean
    fun webhookAuthorizerFilter(@Value("\${back4app.webhook-key}") webhookKey: String): FilterRegistrationBean<ParseWebhookAuthorizerFilter> {
        return FilterRegistrationBean<ParseWebhookAuthorizerFilter>().apply {
            filter = ParseWebhookAuthorizerFilter(webhookKey)
            urlPatterns = listOf("/webhooks/*")
        }
    }

    @Bean
    fun gitHubUserDecorator(): GitHubAuthorizer {
        return GitHubAuthorizer()
    }
}