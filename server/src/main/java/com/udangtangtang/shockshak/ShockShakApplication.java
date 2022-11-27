package com.udangtangtang.shockshak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ShockShakApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShockShakApplication.class, args);
    }

}
