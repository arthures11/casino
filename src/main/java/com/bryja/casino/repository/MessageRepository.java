package com.bryja.casino.repository;

import com.bryja.casino.classes.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findFirst20ByOrderByIdDesc();
    List<Message> findFirst20ByChatroomOrderByIdDesc(String chatroom);

}
