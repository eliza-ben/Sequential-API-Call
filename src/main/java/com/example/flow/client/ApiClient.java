package com.example.flow.client;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

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

  /** Step2: POST XLSX bytes with query params: contentMD5, fileLength, fileName, autoDuplicateFile */
  public <T> ResponseEntity<T> postBytesExpectJsonWithQuery(
      String baseUrl,
      String path,
      String bearer,
      byte[] bytes,
      MediaType contentType,
      String contentMD5,
      long fileLength,
      String fileName,
      boolean autoDuplicateFile,
      Class<T> clazz
  ) {
    String url = UriComponentsBuilder.fromHttpUrl(baseUrl + path)
        .queryParam("contentMD5", contentMD5)
        .queryParam("fileLength", fileLength)
        .queryParam("fileName", fileName)
        .queryParam("autoDuplicateFile", autoDuplicateFile)
        .build(true)
        .toUriString();

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

  /** Step4: PUT no body -> JSON response */
  public <T> ResponseEntity<T> putNoBodyExpectJson(String url, String bearer, Class<T> clazz) {
    return web.put()
        .uri(url)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearer)
        .header(HttpHeaders.CACHE_CONTROL, "no-cache")
        .accept(MediaType.APPLICATION_JSON)
        .exchangeToMono(resp -> resp.toEntity(clazz))
        .block();
  }

  /** Step3: PUT to S3 presigned URL with headers: Content-MD5, file-length (plus Content-Length) */
  public ResponseEntity<Void> putBytesToPresigned(
      String presignedUrl,
      byte[] bytes,
      MediaType contentType,
      String contentMD5,
      long fileLength
  ) {
    return web.put()
        .uri(presignedUrl)
        .header("Content-MD5", contentMD5)
        .header("file-length", String.valueOf(fileLength))
        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLength))
        .contentType(contentType)
        .bodyValue(bytes)
        .exchangeToMono(resp -> resp.toBodilessEntity())
        .block();
  }
}
