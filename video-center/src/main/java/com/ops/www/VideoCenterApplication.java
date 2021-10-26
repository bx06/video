package com.ops.www;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author wangzr
 */
@EnableCircuitBreaker
@EnableDiscoveryClient
@SpringBootApplication
public class VideoCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoCenterApplication.class, args);
    }
}
