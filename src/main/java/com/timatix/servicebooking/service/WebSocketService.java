package com.timatix.servicebooking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendProgressUpdate(Long serviceRequestId, String message) {
        messagingTemplate.convertAndSend("/topic/progress/" + serviceRequestId, message);
        log.info("Sent WebSocket progress update for service request: {}", serviceRequestId);
    }

    public void sendQuoteNotification(Long clientId, Map<String, Object> quoteData) {
        messagingTemplate.convertAndSend("/topic/quotes/" + clientId, quoteData);
        log.info("Sent WebSocket quote notification to client: {}", clientId);
    }

    public void sendGeneralNotification(String channel, Object data) {
        messagingTemplate.convertAndSend("/topic/" + channel, data);
        log.info("Sent WebSocket notification to channel: {}", channel);
    }
}