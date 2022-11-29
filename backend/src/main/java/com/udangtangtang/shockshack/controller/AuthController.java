package com.udangtangtang.shockshack.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.udangtangtang.shockshack.domain.ApplicationUser;
import com.udangtangtang.shockshack.dto.AuthDto;
import com.udangtangtang.shockshack.dto.TokenDto;
import com.udangtangtang.shockshack.service.interfaces.AuthService;
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
import java.util.List;
import java.util.UUID;

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

    @PostMapping("/registration/google")
    public ResponseEntity<Void> validateIdToken(@RequestBody TokenDto dto) throws GeneralSecurityException, IOException {
        try {
            String idTokenString = dto.getIdToken();
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(List.of("171803808918-qnc80jkj9s0gehsj273rhns3l8btvqrd.apps.googleusercontent.com"))
                    .build();
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                log.info("id token received : {}", email);
                authService.register(new AuthDto(email, UUID.randomUUID().toString(), ApplicationUser.UserType.GOOGLE));
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
