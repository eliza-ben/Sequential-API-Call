package com.example.flow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenResponse {
  @JsonProperty("access_token")
  public String accessToken;

  @JsonProperty("token_type")
  public String tokenType;

  @JsonProperty("expires_in")
  public Integer expiresIn;
}
