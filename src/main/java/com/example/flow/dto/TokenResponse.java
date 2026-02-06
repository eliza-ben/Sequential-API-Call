package com.example.flow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TokenResponse {
  @JsonProperty("access_token")
  public String accessToken;

  @JsonProperty("refresh_token")
  public String refreshToken;

  public List<String> roles;
  public String scope;

  @JsonProperty("token_type")
  public String tokenType;

  @JsonProperty("expires_in")
  public Integer expiresIn;
}

