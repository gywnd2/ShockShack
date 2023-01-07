package com.udangtangtang.shockshack.controller;

import com.udangtangtang.shockshack.domain.ApplicationUser;
import com.udangtangtang.shockshack.dto.AuthDto;
import com.udangtangtang.shockshack.dto.TokenDto;
import com.udangtangtang.shockshack.service.interfaces.AuthService;
import com.udangtangtang.shockshack.utils.JwtTokenVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenVerifier verifier;

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

    @PostMapping("/registration/google")
    public ResponseEntity<Void> validateIdToken(@RequestBody TokenDto dto) throws GeneralSecurityException, IOException {
        try {
            log.info("registration with google");
            String idTokenString = dto.getIdToken();
            String username = verifier.verifyGoogleTokenAndGetUsername(idTokenString);
            authService.register(new AuthDto(username, UUID.randomUUID().toString(), ApplicationUser.UserType.GOOGLE));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
