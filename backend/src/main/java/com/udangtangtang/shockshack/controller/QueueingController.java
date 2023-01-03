package com.udangtangtang.shockshack.controller;

import com.udangtangtang.shockshack.domain.ChatRequest;
import com.udangtangtang.shockshack.domain.ChatResponse;
import com.udangtangtang.shockshack.dto.CurrentUsers;
import com.udangtangtang.shockshack.service.QueueingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class QueueingController {

    private final QueueingService queueingService;

    @PostMapping("/join")
    public DeferredResult<ChatResponse> enter(HttpServletRequest request, Authentication authentication) {
        String sessionId = request.getSession().getId();
        DeferredResult<ChatResponse> joinResult = new DeferredResult<>(3000L);

        ChatRequest chatRequest = new ChatRequest(sessionId, (String) authentication.getPrincipal());
        queueingService.joinChatRoom(chatRequest, joinResult);

        joinResult.onTimeout(()->{
            queueingService.timeout(chatRequest);
        });

        return joinResult;
    }

    @PostMapping("/cancel")
    public ResponseEntity<ChatRequest> cancel(HttpServletRequest request, Authentication authentication) {
        String sessionId = request.getSession().getId();
        ChatRequest chatRequest = new ChatRequest(sessionId, (String) authentication.getPrincipal());
        queueingService.cancelChatRoom(chatRequest);
        return ResponseEntity.ok(chatRequest);
    }

    @PostMapping("/current")
    public ResponseEntity<CurrentUsers> currentUsers() {
        return ResponseEntity.ok(new CurrentUsers(queueingService.getCurrentUsers()));
    }

}
