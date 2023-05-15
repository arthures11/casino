package com.bryja.casino.repository;

import com.bryja.casino.classes.Bonuses;
import com.bryja.casino.classes.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BonusesRepository extends JpaRepository<Bonuses, Long> {

    Bonuses findByName(String bonus_name);
}
