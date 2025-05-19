package com.carrental.dto.response;

import com.carrental.model.ChatMessage;
import com.carrental.model.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponse {
    private Long id;
    private User sender;
    private User receiver;
    private String content;
    private LocalDateTime timestamp;
    private boolean read;
    private ChatMessage.MessageType type;
} 