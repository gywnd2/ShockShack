package com.udangtangtang.shockshack.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtTokenVerifier {

    private final String audience = "171803808918-qnc80jkj9s0gehsj273rhns3l8btvqrd.apps.googleusercontent.com";
    public Optional<String> verifyAndGetUsername(String token) throws IOException {
        try {
            Algorithm algorithm = Algorithm.HMAC256("ShockShack".getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return Optional.of(decodedJWT.getSubject());
        } catch (AlgorithmMismatchException e) {
            try{
                return Optional.of(verifyGoogleTokenAndGetUsername(token));
            }catch (IOException | GeneralSecurityException | NullPointerException ex) {
                return Optional.empty();
            }
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }

    public String verifyGoogleTokenAndGetUsername(String token) throws IOException, GeneralSecurityException, NullPointerException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(List.of(audience))
                .build();
        GoogleIdToken idToken = verifier.verify(token);
        return idToken.getPayload().getEmail();
    }
}
