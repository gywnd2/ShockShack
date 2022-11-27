package com.udangtangtang.shockshak.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@Slf4j
@RestController
@RequestMapping("/api/v1/queue")
public class QueueingController {

    @PostMapping("/enter")
    public DeferredResult<ResponseEntity<?>> enter(Authentication authentication) {
        DeferredResult<ResponseEntity<?>> result = new DeferredResult<>(15000L);
        log.info("authentication : {}", authentication.getPrincipal());

        return result;
    }

}
