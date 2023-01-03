package com.udangtangtang.shockshack.domain;

public record ChatRequest(String sessionId, String username) {

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ChatRequest) return username.equals(((ChatRequest) obj).username);
        return false;
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
