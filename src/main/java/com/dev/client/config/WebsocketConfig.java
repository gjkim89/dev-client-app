package com.dev.client.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebsocketConfig implements WebSocketConfigurer {
	
	private final WebSocketHandler webSocketHandler;
	
	private final DashSocketHandler dashSocketHandler;
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		
		registry.addHandler(webSocketHandler, "/websocket")
		.setAllowedOrigins("*");
		
		registry.addHandler(dashSocketHandler, "/dashsocket")
		.setAllowedOrigins("*");

	}

}
