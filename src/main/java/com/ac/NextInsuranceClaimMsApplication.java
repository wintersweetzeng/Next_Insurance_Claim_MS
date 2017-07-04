package com.ac;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NextInsuranceClaimMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NextInsuranceClaimMsApplication.class, args);
    }

    @Bean("objectMapper")
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();

    }
}
