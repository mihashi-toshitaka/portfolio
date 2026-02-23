package com.mihashi_toshitaka.portfolio.common.security;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  private static final String[] PUBLIC_PATHS = {
    "/api/public/**",
    "/html/privacy.html",
    "/actuator/health",
    "/actuator/health/**",
    "/azure", // 無限ループ防止
    "/img/**",
    "/favicon.ico",
    "/menu",
  };

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      ObjectProvider<ClientRegistrationRepository> clientRegistrationRepository,
      CustomSuccessHandler customSuccessHandler)
      throws Exception {
    configureAuthorization(http);
    if (clientRegistrationRepository.getIfAvailable() != null) {
      configureOAuth2Login(http, customSuccessHandler);
      configureLogout(http);
    }
    return http.build();
  }

  private void configureAuthorization(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
        auth ->
            auth.requestMatchers(PUBLIC_PATHS)
                .permitAll()
                // 認証デモページは認証必須
                .requestMatchers("/secured/**")
                .authenticated()
                // ポートフォリオは公開サイトなので残りも認証不要
                .anyRequest()
                .permitAll());
  }

  private void configureOAuth2Login(HttpSecurity http, CustomSuccessHandler customSuccessHandler)
      throws Exception {
    http.oauth2Login(
        oauth2 ->
            oauth2
                .loginPage("/oauth2/authorization/azure")
                .failureUrl("/azure?error")
                .successHandler(customSuccessHandler));
  }

  private void configureLogout(HttpSecurity http) throws Exception {
    http.logout(
        logout ->
            logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/menu")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID"));
  }
}
