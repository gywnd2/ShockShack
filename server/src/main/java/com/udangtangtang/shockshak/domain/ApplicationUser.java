package com.udangtangtang.shockshak.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor @AllArgsConstructor
public class ApplicationUser {

    @Id
    private String email;
    @Setter
    private String password;
    @Enumerated(EnumType.STRING)
    private UserType userType;

    public enum UserType {
        GOOGLE, STANDARD
    }

    @PrePersist
    public void prePersist() {
        if(this.userType == null) this.userType = UserType.STANDARD;
    }
}
