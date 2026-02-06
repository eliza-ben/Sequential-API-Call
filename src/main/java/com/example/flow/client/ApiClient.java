package com.example.flow.client;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.MessageDigest;
import java.util.Base64;

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
        .header(HttpHeaders.CACHE_CONTROL, "no-cache")
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
        .header(HttpHeaders.CACHE_CONTROL, "no-cache")
        .accept(MediaType.APPLICATION_JSON)
        .exchangeToMono(resp -> resp.toEntity(clazz))
        .block();
  }

  /** Step2: POST XLSX bytes to API -> get JSON response */
  public <T> ResponseEntity<T> postBytesExpectJson(String url, String bearer, byte[] bytes, MediaType contentType, Class<T> clazz) {
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

  /** Step4: PUT no body -> get JSON response */
  public <T> ResponseEntity<T> putNoBodyExpectJson(String url, String bearer, Class<T> clazz) {
    return web.put()
        .uri(url)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearer)
        .header(HttpHeaders.CACHE_CONTROL, "no-cache")
        .accept(MediaType.APPLICATION_JSON)
        .exchangeToMono(resp -> resp.toEntity(clazz))
        .block();
  }

  /** Step3: PUT to S3 presigned URL with Content-MD5 + Content-Type */
  public ResponseEntity<Void> putBytesToPresigned(String presignedUrl, byte[] bytes, MediaType contentType) {
    String contentMd5 = base64Md5(bytes);

    return web.put()
        .uri(presignedUrl)
        .header("Content-MD5", contentMd5)
        .contentType(contentType)
        .bodyValue(bytes)
        .exchangeToMono(resp -> resp.toBodilessEntity())
        .block();
  }

  private static String base64Md5(byte[] bytes) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] digest = md.digest(bytes);
      return Base64.getEncoder().encodeToString(digest);
    } catch (Exception e) {
      throw new RuntimeException("Failed to compute MD5", e);
    }
  }
}
