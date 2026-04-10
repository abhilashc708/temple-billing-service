package com.example.temple_billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TempleBillingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TempleBillingApplication.class, args);
    }

}
