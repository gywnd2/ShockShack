package com.udangtangtang.shockshak.dto;

import com.udangtangtang.shockshak.domain.ApplicationUser;

public record AuthDto(String email, String password, ApplicationUser.UserType userType) {
    public AuthDto(String email, String password) {
        this(email, password, ApplicationUser.UserType.STANDARD);
    }
}
