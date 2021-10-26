package com.ops.www;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author wangzr
 */
@EnableEurekaServer
@SpringBootApplication
public class VideoRegisterApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoRegisterApplication.class, args);
    }
}
