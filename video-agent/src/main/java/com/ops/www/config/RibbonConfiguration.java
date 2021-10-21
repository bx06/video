package com.ops.www.config;

import com.netflix.loadbalancer.AvailabilityFilteringRule;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.PingUrl;
import org.springframework.context.annotation.Bean;

/**
 * @author wangzr
 */
public class RibbonConfiguration {

    @Bean
    public IPing ribbonPing() {
        return new PingUrl();
    }

    @Bean
    public IRule ribbonRule() {
        return new AvailabilityFilteringRule();
    }
}
