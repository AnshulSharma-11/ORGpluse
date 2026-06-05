package com.orgpluse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ANSHULApplication {

    public static void main(String[] args) {
        SpringApplication.run(ANSHULApplication.class, args);
    }

}
