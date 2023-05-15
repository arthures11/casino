package com.bryja.casino.repository;

import com.bryja.casino.classes.BonusHistory;
import com.bryja.casino.classes.DiceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BonusHistoryRepository extends JpaRepository<BonusHistory, Long> {

    public List<BonusHistory> findFirst20ByUserIdOrderByIdDesc(Long userId);
}
