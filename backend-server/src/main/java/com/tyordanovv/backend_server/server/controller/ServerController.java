package com.tyordanovv.backend_server.server.controller;

import com.tyordanovv.backend_server.server.model.ServerTestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/server/")
@Slf4j
public class ServerController {
    @GetMapping("get-method")
    public ResponseEntity<?> getMethodServer() {
        ServerTestDto serverResponse = new ServerTestDto(
                "/api/server/get-method",
                200,
                "Response of the server get method.");
        return ResponseEntity.ok(serverResponse);
    }
    @PostMapping("post-method")
    public ResponseEntity<?> postMethodServer() {
        ServerTestDto serverResponse = new ServerTestDto(
                "/api/server/post-method",
                200,
                "Response of the server POST method.");
        return ResponseEntity.ok(serverResponse);
    }
}
