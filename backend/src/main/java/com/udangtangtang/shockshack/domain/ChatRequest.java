package com.udangtangtang.shockshack.domain;

public record ChatRequest(String sessionId, String username) {
    @Override
    public boolean equals(Object o) {
        if(o instanceof ChatRequest) return username.equals(((ChatRequest) o).username);
        return false;
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
