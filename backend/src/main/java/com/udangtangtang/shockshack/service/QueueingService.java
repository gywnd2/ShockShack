package com.udangtangtang.shockshack.service;

import com.udangtangtang.shockshack.domain.ChatMessage;
import com.udangtangtang.shockshack.domain.ChatRequest;
import com.udangtangtang.shockshack.domain.ChatResponse;
import com.udangtangtang.shockshack.domain.ChatResponse.ResponseResult;
import com.udangtangtang.shockshack.domain.MessageType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueingService {

    private final SimpMessagingTemplate messagingTemplate;

    private Map<ChatRequest, DeferredResult<ChatResponse>> waitingUsers;
    private Map<String, String> connectedUsers;
    private ReentrantReadWriteLock lock;

    @PostConstruct
    private void setUp() {
        this.waitingUsers = new LinkedHashMap<>();
        this.lock = new ReentrantReadWriteLock();
        this.connectedUsers = new ConcurrentHashMap<>();
    }

    @Async
    public void joinChatRoom(ChatRequest request, DeferredResult<ChatResponse> deferredResult) {
        if (request == null || deferredResult == null) {
            return;
        }

        try {
            lock.writeLock().lock();
            waitingUsers.put(request, deferredResult);
        } finally {
            lock.writeLock().unlock();
            establishChatRoom();
        }
    }

    public void cancelChatRoom(ChatRequest chatRequest) {
        try {
            lock.writeLock().lock();
            setJoinResult(waitingUsers.remove(chatRequest), new ChatResponse(ResponseResult.CANCEL, null, chatRequest.sessionId()));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void timeout(ChatRequest chatRequest) {
        try {
            lock.writeLock().lock();
            setJoinResult(waitingUsers.remove(chatRequest), new ChatResponse(ResponseResult.TIMEOUT, null, chatRequest.sessionId()));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void establishChatRoom() {
        try {
            lock.readLock().lock();
            if (waitingUsers.size() < 2) {
                return;
            }

            Iterator<ChatRequest> itr = waitingUsers.keySet().iterator();
            ChatRequest user1 = itr.next();
            ChatRequest user2 = itr.next();

            String uuid = UUID.randomUUID().toString();

            DeferredResult<ChatResponse> user1Result = waitingUsers.remove(user1);
            DeferredResult<ChatResponse> user2Result = waitingUsers.remove(user2);

            user1Result.setResult(new ChatResponse(ResponseResult.SUCCESS, uuid, user1.sessionId()));
            user2Result.setResult(new ChatResponse(ResponseResult.SUCCESS, uuid, user2.sessionId()));
        } catch (Exception e) {
            log.warn("Exception occur while checking waiting users", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void sendMessage(String chatRoomId, ChatMessage chatMessage) {
        String destination = getDestination(chatRoomId);
        messagingTemplate.convertAndSend(destination, chatMessage);
    }

    public void connectUser(String chatRoomId, String sessionId) {
        connectedUsers.put(sessionId, chatRoomId);
    }

    public void disconnectUser(String sessionId) {
        try{
            lock.writeLock().lock();
            String chatRoomId = connectedUsers.remove(sessionId);
            ChatMessage chatMessage = new ChatMessage();

            chatMessage.setMessageType(MessageType.DISCONNECTED);
            sendMessage(chatRoomId, chatMessage);
        } finally {
            lock.writeLock().unlock();
        }


    }

    private String getDestination(String chatRoomId) {
        return "/topic/" + chatRoomId;
    }

    private void setJoinResult(DeferredResult<ChatResponse> result, ChatResponse response) {
        if (result != null) {
            switch (response.getResponseResult()) {
                case CANCEL -> result.setResult(response);
                case TIMEOUT -> result.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(response));
                case DUPLICATED -> result.setErrorResult(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
            }
        }
    }

    public int getCurrentUsers() {
        return connectedUsers.size();
    }

}
