package com.bryja.casino.repository;

import com.bryja.casino.classes.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}