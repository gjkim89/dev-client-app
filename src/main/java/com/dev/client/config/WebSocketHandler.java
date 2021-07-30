package com.dev.client.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
	
private static List<WebSocketSession> sessionList = new ArrayList<WebSocketSession>();
	
	
	//웹소켓 접속시 실행 메소드
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		
		sessionList.add(session);
		log.info("접속자 : {}", session.getId());
		
	}
	
	//메세지 수신 시 실행 메소드
	//-message: 사용자가 전송한 메시지 정보
	//-payload: 실제 보낸 내용
	//-byteCount: 보낸 메시지 크기
	//-last: 메시지 종료 여부
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		
		log.info("받은 메시지 : {}", message.getPayload());
		
		for(WebSocketSession sess : sessionList) {
			sess.sendMessage(new TextMessage(message.getPayload()));
		}
		
	}
	
	//사용자 접속 종료시 실행 메소드
	// - status : 접속 종료된 원인과 정보
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		
		log.info("접속 종료 : {}", session.getId());
		sessionList.remove(session);
		
	}
	
	//에러 발생시 실행 메소드
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		
		log.info("Error : {}", exception.toString());
	}
	
}
