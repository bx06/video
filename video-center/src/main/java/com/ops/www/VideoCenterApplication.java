package com.ops.www;

import com.ops.www.center.config.RibbonConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

/**
 * @author wangzr
 */
@EnableCircuitBreaker
@EnableDiscoveryClient
@RibbonClient(name = "video-center", configuration = RibbonConfiguration.class)
@SpringBootApplication
public class VideoCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoCenterApplication.class, args);
    }
}
