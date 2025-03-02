package com.chatApp.chat.config;

import com.chatApp.chat.model.Status;
import org.apache.logging.log4j.message.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    public void handleUserDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (Objects.nonNull(username)) {
            log.info("User disconnected: {}", username);

            Message chatMessage = Message.builder()
                    .senderName(username)
                    .message("User has left the chat")
                    .timestamp(LocalDateTime.now())
                    .status(Status.LEAVE)
                    .build();

            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}
