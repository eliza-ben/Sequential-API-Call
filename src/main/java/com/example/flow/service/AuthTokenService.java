package com.example.flow.service;

import com.example.flow.dto.TokenResponse;
import com.example.flow.util.ApiAssert;
import com.example.flow.util.ApiFlowException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ClientResponse;

@Service
public class AuthTokenService {

  private final WebClient web;
  private final String tokenPath;

  @Value("${auth.client-id}") private String clientId;
  @Value("${auth.client-secret}") private String clientSecret;
  @Value("${auth.username}") private String username;
  @Value("${auth.password}") private String password;

  @Value("${flow.success-token-type:Bearer}") private String expectedTokenType;

  public AuthTokenService(WebClient.Builder builder,
                          @Value("${auth.base-url}") String baseUrl,
                          @Value("${auth.token-path}") String tokenPath) {
    this.web = builder.baseUrl(baseUrl).build();
    this.tokenPath = tokenPath;
  }

  public TokenResponse getTokenStrict() {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "password");
    form.add("client_id", clientId);
    form.add("client_secret", clientSecret);
    form.add("username", username);
    form.add("password", password);

    ResponseEntity<TokenResponse> resp = web.post()
        .uri(tokenPath)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .accept(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.CACHE_CONTROL, "no-cache")     // matches your screenshot
        .header(HttpHeaders.USER_AGENT, "SpringWebClient") // can keep PostmanRuntime if you want
        .bodyValue(form)
        .exchangeToMono(r -> r.toEntity(TokenResponse.class))
        .block();

    if (resp == null) throw new ApiFlowException("Step1 failed: null response");

    // ✅ MUST be 200
    ApiAssert.require200(resp.getStatusCodeValue(), "Step1");

    TokenResponse tr = resp.getBody();
    if (tr == null) throw new ApiFlowException("Step1 failed: empty body");

    // ✅ access token must exist for Step2/4/5/6
    ApiAssert.requireNotBlank(tr.accessToken, "Step1", "access_token");

    // Optional but recommended: token_type should be Bearer
    if (tr.tokenType == null || !tr.tokenType.trim().equalsIgnoreCase(expectedTokenType)) {
      throw new ApiFlowException("Step1 failed: token_type=" + tr.tokenType + " (expected " + expectedTokenType + ")");
    }

    return tr;
  }
}
