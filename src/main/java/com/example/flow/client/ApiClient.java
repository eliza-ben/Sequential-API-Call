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

  /**
   * Step2: POST file bytes with query params:
   *   contentMD5, fileLength, fileName, autoDuplicateFile
   * AND send contentType as header (both Content-Type and custom contentType).
   */
  public <T> ResponseEntity<T> step2PostBytesWithQueryAndContentTypeHeader(
      String baseUrl,
      String path,
      String bearer,
      byte[] bytes,
      String contentTypeHeaderValue,
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

    MediaType mt = parseMediaTypeOrOctet(contentTypeHeaderValue);

    return web.post()
        .uri(url)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearer)
        .header(HttpHeaders.CACHE_CONTROL, "no-cache")
        // ✅ required by your request: contentType as header in Step2
        .header("contentType", contentTypeHeaderValue)   // custom header (change name if needed)
        .contentType(mt)                                 // standard Content-Type header
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(bytes)
        .exchangeToMono(resp -> resp.toEntity(clazz))
        .block();
  }

  /**
   * Step4: PUT no body -> JSON response.
   */
  public <T> ResponseEntity<T> putNoBodyExpectJson(String url, String bearer, Class<T> clazz) {
    return web.put()
        .uri(url)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearer)
        .header(HttpHeaders.CACHE_CONTROL, "no-cache")
        .accept(MediaType.APPLICATION_JSON)
        .exchangeToMono(resp -> resp.toEntity(clazz))
        .block();
  }

  /**
   * Step3: PUT to presigned URL with headers:
   *   Content-MD5, file-length
   * AND send contentType as header (both Content-Type and custom contentType).
   */
  public ResponseEntity<Void> step3PutBytesToPresignedWithHeaders(
      String presignedUrl,
      byte[] bytes,
      String contentTypeHeaderValue,
      String contentMD5,
      long fileLength
  ) {
    MediaType mt = parseMediaTypeOrOctet(contentTypeHeaderValue);

    return web.put()
        .uri(presignedUrl)
        // ✅ required headers
        .header("Content-MD5", contentMD5)
        .header("file-length", String.valueOf(fileLength))
        // strongly recommended standard length header too
        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLength))
        // ✅ required by your request: contentType as header in Step3
        .header("contentType", contentTypeHeaderValue) // custom header (change name if needed)
        .contentType(mt)                               // standard Content-Type header
        .bodyValue(bytes)
        .exchangeToMono(resp -> resp.toBodilessEntity())
        .block();
  }

  private static MediaType parseMediaTypeOrOctet(String contentTypeHeaderValue) {
    if (contentTypeHeaderValue == null || contentTypeHeaderValue.isBlank()) {
      return MediaType.APPLICATION_OCTET_STREAM;
    }
    try {
      return MediaType.parseMediaType(contentTypeHeaderValue);
    } catch (Exception e) {
      return MediaType.APPLICATION_OCTET_STREAM;
    }
  }
}
