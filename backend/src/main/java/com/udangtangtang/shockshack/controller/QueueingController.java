package com.udangtangtang.shockshack.controller;

import com.udangtangtang.shockshack.domain.ChatRequest;
import com.udangtangtang.shockshack.domain.ChatResponse;
import com.udangtangtang.shockshack.service.QueueingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public DeferredResult<ChatResponse> enter(HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        log.info("sessionId : {}", sessionId);
        DeferredResult<ChatResponse> joinResult = new DeferredResult<>(15000L);

        ChatRequest chatRequest = new ChatRequest(sessionId);
        queueingService.joinChatRoom(chatRequest, joinResult);
        joinResult.onTimeout(()->{
            log.info("session {} timeout", sessionId);
            queueingService.cancelChatRoom(chatRequest);
        });
        return joinResult;
    }

}
