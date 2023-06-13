package com.bryja.casino.repository;

import com.bryja.casino.classes.CoinflipHistory;
import com.bryja.casino.classes.DiceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoinflipHistoryRepository extends JpaRepository<CoinflipHistory, Long> {

    public List<CoinflipHistory> findFirst20ByUserIdOrderByIdDesc(Long userId);
}
