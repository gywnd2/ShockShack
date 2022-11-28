package com.udangtangtang.shockshak.controller;

import com.udangtangtang.shockshak.domain.ChatRequest;
import com.udangtangtang.shockshak.domain.ChatResponse;
import com.udangtangtang.shockshak.service.QueueingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;

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

        queueingService.joinChatRoom(new ChatRequest(sessionId), joinResult);
        return joinResult;
    }

}
