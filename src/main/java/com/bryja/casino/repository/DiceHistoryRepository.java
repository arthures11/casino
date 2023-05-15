package com.bryja.casino.repository;

import com.bryja.casino.classes.DiceHistory;
import com.bryja.casino.classes.Message;
import com.bryja.casino.classes.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiceHistoryRepository extends JpaRepository<DiceHistory, Long> {

    public List<DiceHistory> findFirst20ByOrderByIdDesc();
    public List<DiceHistory> findFirst20ByUserIdOrderByIdDesc(Long userId);
}
