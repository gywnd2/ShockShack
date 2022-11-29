package com.udangtangtang.shockshack.dto;


import com.udangtangtang.shockshack.domain.ApplicationUser;

public record AuthDto(String email, String password, ApplicationUser.UserType userType) {
    public AuthDto(String email, String password) {
        this(email, password, ApplicationUser.UserType.STANDARD);
    }
}
