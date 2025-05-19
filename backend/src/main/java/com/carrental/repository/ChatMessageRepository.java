package com.carrental.repository;

import com.carrental.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT m FROM ChatMessage m WHERE " +
           "(m.sender.id = :userId1 AND m.receiver.id = :userId2) OR " +
           "(m.sender.id = :userId2 AND m.receiver.id = :userId1) " +
           "ORDER BY m.timestamp ASC")
    List<ChatMessage> findMessagesBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT DISTINCT m FROM ChatMessage m " +
           "WHERE m.id IN (" +
           "   SELECT MAX(m2.id) FROM ChatMessage m2 " +
           "   WHERE (m2.sender.id = :userId OR m2.receiver.id = :userId) " +
           "   GROUP BY " +
           "       CASE " +
           "           WHEN m2.sender.id = :userId THEN m2.receiver.id " +
           "           ELSE m2.sender.id " +
           "       END" +
           ") " +
           "ORDER BY m.timestamp DESC")
    List<ChatMessage> findLatestMessagesForUser(@Param("userId") Long userId);

    @Query("SELECT DISTINCT m FROM ChatMessage m " +
           "WHERE m.id IN (" +
           "   SELECT MAX(m2.id) FROM ChatMessage m2 " +
           "   WHERE m2.type = 'CUSTOMER_SERVICE' " +
           "   GROUP BY " +
           "       CASE " +
           "           WHEN m2.sender.role = 'ADMIN' THEN m2.receiver.id " +
           "           ELSE m2.sender.id " +
           "       END" +
           ") " +
           "ORDER BY m.timestamp DESC")
    List<ChatMessage> findLatestMessagesForAdmin();
} 