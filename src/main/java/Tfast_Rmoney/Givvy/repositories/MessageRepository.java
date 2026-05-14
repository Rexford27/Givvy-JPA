package Tfast_Rmoney.Givvy.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import Tfast_Rmoney.Givvy.entities.Message;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findByRecipient_UserIdOrderByCreatedAtDesc(UUID recipientId);

    List<Message> findBySender_UserIdOrderByCreatedAtDesc(UUID senderId);
}