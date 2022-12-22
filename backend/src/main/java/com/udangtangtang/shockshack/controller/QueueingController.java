package com.udangtangtang.shockshack.controller;

import com.udangtangtang.shockshack.domain.ChatRequest;
import com.udangtangtang.shockshack.domain.ChatResponse;
import com.udangtangtang.shockshack.service.QueueingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;


@Slf4j
@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
public class QueueingController {

    private final QueueingService queueingService;

    @PostMapping("/join")
    public DeferredResult<ChatResponse> enter(HttpServletRequest request, Authentication authentication) {
        String sessionId = request.getSession().getId();
        log.info("username : {}", sessionId);
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DeferredResult<ChatResponse> joinResult = new DeferredResult<>(3000L);

        ChatRequest chatRequest = new ChatRequest(sessionId);
        queueingService.joinChatRoom(chatRequest, joinResult);

        joinResult.onTimeout(()->{
            log.info("session {} timeout", sessionId);
            queueingService.timeout(chatRequest);
        });

        return joinResult;
    }

    @PostMapping("/cancel")
    public ResponseEntity<ChatRequest> cancel(HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        ChatRequest chatRequest = new ChatRequest(sessionId);
        queueingService.cancelChatRoom(chatRequest);
        return ResponseEntity.ok(chatRequest);
    }

}
