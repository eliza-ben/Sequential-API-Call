package com.example.flow.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ApiClient {

  private final WebClient web;

  public ApiClient(WebClient.Builder builder) {
    this.web = builder.build();
  }

  public <T> ResponseEntity<T> postJson(String url, String bearer, Object body, Class<T> clazz) {
    return web.post()
        .uri(url)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearer)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .exchangeToMono(resp -> resp.toEntity(clazz))
        .block();
  }

  public <T> ResponseEntity<T> getJson(String url, String bearer, Class<T> clazz) {
    return web.get()
        .uri(url)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearer)
        .accept(MediaType.APPLICATION_JSON)
        .exchangeToMono(resp -> resp.toEntity(clazz))
        .block();
  }

  /** Step2 style: POST XLSX bytes to API, get JSON response back */
  public <T> ResponseEntity<T> postBytesExpectJson(
      String url, String bearer, byte[] bytes, MediaType contentType, Class<T> clazz
  ) {
    return web.post()
        .uri(url)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearer)
        .header(HttpHeaders.CACHE_CONTROL, "no-cache")
        .contentType(contentType)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(bytes)
        .exchangeToMono(resp -> resp.toEntity(clazz))
        .block();
  }

  /** Step3 style: Upload bytes to presigned URL (often PUT). */
  public ResponseEntity<Void> putBytesToPresigned(String presignedUrl, byte[] bytes, MediaType contentType) {
    return web.put()
        .uri(presignedUrl)
        .contentType(contentType)
        .bodyValue(bytes)
        .exchangeToMono(resp -> resp.toBodilessEntity())
        .block();
  }
}
