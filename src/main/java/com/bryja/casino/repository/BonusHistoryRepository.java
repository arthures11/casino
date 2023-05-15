package com.bryja.casino.repository;

import com.bryja.casino.classes.BonusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BonusHistoryRepository extends JpaRepository<BonusHistory, Long> {
}
