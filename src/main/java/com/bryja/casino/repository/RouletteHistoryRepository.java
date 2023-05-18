package com.bryja.casino.repository;

import com.bryja.casino.classes.DiceHistory;
import com.bryja.casino.classes.RouletteHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouletteHistoryRepository extends JpaRepository<RouletteHistory, Long> {
    public List<RouletteHistory> findFirst20ByUserIdOrderByIdDesc(Long userId);
}
