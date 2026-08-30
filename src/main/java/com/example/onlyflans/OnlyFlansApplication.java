package com.example.onlyflans;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OnlyFlansApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlyFlansApplication.class, args);
    }

}