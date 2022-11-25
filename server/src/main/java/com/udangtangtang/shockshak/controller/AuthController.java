package com.udangtangtang.shockshak.controller;

import com.udangtangtang.shockshak.dto.AuthDto;
import com.udangtangtang.shockshak.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registration")
    public ResponseEntity<String> registration(@RequestBody AuthDto dto) {
        try {
            log.info("registration with : {}", dto);
            authService.register(dto);
            return ResponseEntity.ok(dto.email());
        } catch (IllegalStateException | IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(dto.email());
        }
    }

}
