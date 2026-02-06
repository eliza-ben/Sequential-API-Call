package com.example.flow.controller;

import com.example.flow.dto.FlowResult;
import com.example.flow.service.FlowOrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/flow")
public class FlowController {

  private final FlowOrchestratorService flow;

  public FlowController(FlowOrchestratorService flow) {
    this.flow = flow;
  }

  @PostMapping("/run")
  public ResponseEntity<FlowResult> run(@RequestPart("file") MultipartFile file) throws Exception {
    byte[] bytes = file.getBytes();
    FlowResult result = flow.runAll(bytes, file.getOriginalFilename());
    return ResponseEntity.ok(result);
  }
}
