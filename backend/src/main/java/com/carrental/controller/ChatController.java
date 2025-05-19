package com.carrental.controller;

import com.carrental.dto.request.ChatMessageRequest;
import com.carrental.dto.response.ChatMessageResponse;
import com.carrental.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @RequestBody ChatMessageRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(chatService.saveMessage(request, userDetails.getUsername()));
    }

    @GetMapping("/messages/{userId}")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(chatService.getMessages(userId, userDetails.getUsername()));
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ChatMessageResponse>> getConversations(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(chatService.getConversations(userDetails.getUsername()));
    }

    @PutMapping("/messages/{messageId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long messageId,
            @AuthenticationPrincipal UserDetails userDetails) {
        chatService.markAsRead(messageId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
} 