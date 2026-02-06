package com.example.flow.service;

import com.example.flow.dto.TokenResponse;
import com.example.flow.util.ApiFlowException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.*;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AuthTokenService {

  private final WebClient web;
  private final String tokenPath;

  @Value("${auth.client-id}") private String clientId;
  @Value("${auth.client-secret}") private String clientSecret;
  @Value("${auth.username}") private String username;
  @Value("${auth.password}") private String password;

  public AuthTokenService(WebClient.Builder builder,
                          @Value("${auth.base-url}") String baseUrl,
                          @Value("${auth.token-path}") String tokenPath) {
    this.web = builder.baseUrl(baseUrl).build();
    this.tokenPath = tokenPath;
  }

  public TokenResponse getToken() {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "password");
    form.add("client_id", clientId);
    form.add("client_secret", clientSecret);
    form.add("username", username);
    form.add("password", password);

    TokenResponse tr = web.post()
        .uri(tokenPath)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(form)
        .retrieve()
        .bodyToMono(TokenResponse.class)
        .block();

    if (tr == null || tr.accessToken == null || tr.accessToken.isBlank()) {
      throw new ApiFlowException("Step1 failed: access_token missing");
    }
    return tr;
  }
}
