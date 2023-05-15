package com.bryja.casino.repository;

import com.bryja.casino.classes.CoinflipHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinflipHistoryRepository extends JpaRepository<CoinflipHistory, Long> {
}
