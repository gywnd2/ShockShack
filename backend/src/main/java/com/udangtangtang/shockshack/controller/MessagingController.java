package com.udangtangtang.shockshack.controller;

import com.udangtangtang.shockshack.domain.ChatMessage;
import com.udangtangtang.shockshack.domain.MessageType;
import com.udangtangtang.shockshack.service.QueueingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessagingController {

    private final QueueingService queueingService;

    @MessageMapping("/{chatRoomId}")
    public void sendMessage(@DestinationVariable("chatRoomId") String chatRoomId, @Payload ChatMessage chatMessage) {
        log.info("Request message. roomd id : {} | chat message : {} | principal : {}", chatRoomId, chatMessage);
        if (!StringUtils.hasText(chatRoomId) || chatMessage == null) {
            return;
        }

        if (chatMessage.getMessageType() == MessageType.CHAT) {
            queueingService.sendMessage(chatRoomId, chatMessage);
        }
    }
}
