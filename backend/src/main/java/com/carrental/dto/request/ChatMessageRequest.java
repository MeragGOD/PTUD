package com.carrental.dto.request;

import lombok.Data;
 
@Data
public class ChatMessageRequest {
    private String receiverEmail;
    private String content;
} 