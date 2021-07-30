package com.dev.client.config;

import java.io.IOException;
import java.util.Collections;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

//@Component
@Configuration
public class RestTemplateConfig {
	
//	@Bean
//	public RestTemplate restTemplate() {
//		return new RestTemplate();
//	}
	
	@Bean
	public RestTemplate restTemplate() {
		
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		
		HttpClient client = HttpClientBuilder.create()
				.setMaxConnTotal(50)
				.setMaxConnPerRoute(20)
				.build();
		
		factory.setHttpClient(client);
		factory.setConnectTimeout(2000);
		factory.setReadTimeout(5000);
		
		RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(factory));
		restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
		
		return restTemplate;
	}
	
}

class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		
		// 전
		ClientHttpResponse response = execution.execute(request, body);
		// 후
		
		return response;
	}
	
}
