package com.ops.www.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author wangzr
 */
@Configuration
public class RestTemplateConfig {

	@Bean(name = "restTemplate")
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean(name = "restSslTemplate")
	public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
		return new RestTemplate(factory);
	}

	@Bean
	public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
		SecureSocketsLayer factory = new SecureSocketsLayer();
		factory.setReadTimeout(5000);
		factory.setConnectTimeout(6000);
		return factory;
	}
}
