package com.carrental.service;

import com.carrental.dto.request.ChatMessageRequest;
import com.carrental.dto.response.ChatMessageResponse;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.model.ChatMessage;
import com.carrental.model.User;
import com.carrental.repository.ChatMessageRepository;
import com.carrental.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessageResponse saveMessage(ChatMessageRequest request, String senderUsername) {
        User sender = userRepository.findByEmail(senderUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
        User receiver = userRepository.findByEmail(request.getReceiverEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        // Check if sender is admin or receiver is admin
        if (!sender.getRole().name().equals("ADMIN") && !receiver.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("Messages can only be sent to or from admin");
        }

        ChatMessage message = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .timestamp(LocalDateTime.now())
                .read(false)
                .type(ChatMessage.MessageType.CUSTOMER_SERVICE)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);
        return mapToResponse(savedMessage);
    }

    public List<ChatMessageResponse> getMessages(Long userId, String currentUsername) {
        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User otherUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if current user is admin or is part of the conversation
        if (!currentUser.getRole().name().equals("ADMIN") && 
            !currentUser.getId().equals(userId) && 
            !otherUser.getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You can only view your own conversations");
        }

        List<ChatMessage> messages = chatMessageRepository.findMessagesBetweenUsers(currentUser.getId(), otherUser.getId());
        return messages.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ChatMessageResponse> getConversations(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // If user is admin, get all conversations
        if (user.getRole().name().equals("ADMIN")) {
            List<ChatMessage> latestMessages = chatMessageRepository.findLatestMessagesForAdmin();
            return latestMessages.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }

        // If user is not admin, get only their conversations with admin
        List<ChatMessage> latestMessages = chatMessageRepository.findLatestMessagesForUser(user.getId());
        return latestMessages.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long messageId, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        if (message.getReceiver().getId().equals(user.getId())) {
            message.setRead(true);
            chatMessageRepository.save(message);
        }
    }

    private ChatMessageResponse mapToResponse(ChatMessage message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .sender(message.getSender())
                .receiver(message.getReceiver())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .read(message.isRead())
                .type(message.getType())
                .build();
    }
} 