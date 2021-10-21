package com.ops.www;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

import com.ops.www.center.config.RibbonConfiguration;
import com.ops.www.common.util.StopWatch;

/**
 * @author wangzr
 */
@EnableCircuitBreaker
@EnableDiscoveryClient
@RibbonClient(name = "video-center", configuration = RibbonConfiguration.class)
@SpringBootApplication
public class VideoCenterApplication {

    private static final Logger logger = LogManager.getLogger();

    private static void printMain(double timeSecond) {
        int sunSize = 100;
        StringBuffer sb = new StringBuffer(3 * sunSize);
        StringBuilder endSb = new StringBuilder(sunSize);
        sb.append("\n");
        String timeStr = "Video-Center startUp in " + timeSecond + "s";
        int number = 2;
        if (timeStr.length() % number != 0) {
            timeStr += " ";
        }
        int timeSize = timeStr.length(), blankSize = (sunSize - timeSize - 2) / 2;
        StringBuilder blankSb = new StringBuilder(blankSize);
        for (int i = 0; i < sunSize; i++) {
            sb.append("*");
            endSb.append("*");
        }
        for (int i = 0; i < blankSize; i++) {
            blankSb.append(" ");
        }
        sb.append("\n*").append(blankSb).append(timeStr).append(blankSb).append("*\n").append(endSb);
        logger.info(sb);
    }

    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        SpringApplication.run(VideoCenterApplication.class, args);
        stopWatch.stop();
        printMain(stopWatch.timeSecond());
    }
}
