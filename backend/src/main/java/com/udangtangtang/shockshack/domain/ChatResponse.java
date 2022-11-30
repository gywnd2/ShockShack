package com.udangtangtang.shockshack.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class ChatResponse {

    private ResponseResult responseResult;
    private String chatRoomId;
    private String sessionId;

    public enum ResponseResult{
        SUCCESS, CANCEL, TIMEOUT
    }
}
