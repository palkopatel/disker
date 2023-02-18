package com.disker.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

@Log4j2
@Configuration
public class DiskConfig {

  @Value("${app.get.resources:20}")
  private int limit;

  @Bean
  @Lazy
  public DiskRestClient diskRestClient(OAuth2AuthorizedClientService clientService) {
    Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();
    String accessToken = null;
    if (authentication.getClass()
            .isAssignableFrom(OAuth2AuthenticationToken.class)) {
      OAuth2AuthenticationToken oauthToken =
              (OAuth2AuthenticationToken) authentication;
      String clientRegistrationId =
              oauthToken.getAuthorizedClientRegistrationId();
      if (clientRegistrationId.equals("yandex")) {
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                    clientRegistrationId, oauthToken.getName());
        accessToken = client.getAccessToken().getTokenValue();
      }
    }
    log.info("Access Token : {}", accessToken);
    return new DiskRestClient(accessToken);
  }

}