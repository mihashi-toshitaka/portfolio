package com.mihashi_toshitaka.portfolio.common.security;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.DefaultSecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  DefaultSecurityFilterChain securityFilterChain(
      HttpSecurity http,
      ObjectProvider<ClientRegistrationRepository> clientRegistrationRepository,
      CustomSuccessHandler customSuccessHandler)
      throws Exception {
    http.authorizeHttpRequests(
        auth ->
            auth
                // 外部向けAPIは認証不要
                .requestMatchers("/api/public/**")
                .permitAll()
                // プライバシーポリシーHTMLは認証不要
                .requestMatchers("/html/privacy.html")
                .permitAll()
                // ヘルスチェックエンドポイントは認証不要
                .requestMatchers("/actuator/health", "/actuator/health/**")
                .permitAll()
                // Azure認証エラーページは認証不要（無限ループ防止）
                .requestMatchers("/azure")
                .permitAll()
                // 静的リソースは認証不要（エラーページから参照されるため）
                .requestMatchers("/img/**", "/favicon.ico")
                .permitAll()
                // メニューはポートフォリオ公開ページ（認証不要）
                .requestMatchers("/menu")
                .permitAll()
                // 認証デモページは認証必須
                .requestMatchers("/secured/**")
                .authenticated()
                // それ以外は認証不要（ポートフォリオは公開サイト）
                .anyRequest()
                .permitAll());
    if (clientRegistrationRepository.getIfAvailable() != null) {
      // 認証時の挙動（デフォルトログイン画面は無効）
      http.oauth2Login(
          oauth2 ->
              oauth2
                  .loginPage("/oauth2/authorization/azure")
                  .failureUrl("/azure?error")
                  .successHandler(customSuccessHandler));
      http.logout(
          logout ->
              logout
                  .logoutUrl("/logout")
                  .logoutSuccessUrl("/menu")
                  .invalidateHttpSession(true)
                  .deleteCookies("JSESSIONID"));
    }
    return http.build();
  }
}
