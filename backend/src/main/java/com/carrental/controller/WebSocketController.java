package com.carrental.controller;

import com.carrental.dto.request.ChatMessageRequest;
import com.carrental.dto.response.ChatMessageResponse;
import com.carrental.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String username = headerAccessor.getUser().getName();
        ChatMessageResponse message = chatService.saveMessage(chatMessage, username);
        
        // Send to specific user
        messagingTemplate.convertAndSendToUser(
            message.getReceiver().getUsername(),
            "/queue/messages",
            message
        );
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessageRequest chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Lấy username từ headerAccessor (nếu có)
        String username = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : null;
        if (username != null) {
            headerAccessor.getSessionAttributes().put("username", username);
        }
    }
} 