package com.dev.client.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class DashSocketHandler extends TextWebSocketHandler {
	
	private static List<WebSocketSession> dashSessionList = new ArrayList<WebSocketSession>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		
		dashSessionList.add(session);
		log.info("dashsocket 접속자 : {}", session.getId());
		
	}
	
}
