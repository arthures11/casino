package com.bryja.casino.controllers;

import com.bryja.casino.classes.Bonuses;
import com.bryja.casino.repository.BonusesRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;


import java.time.Duration;

import java.util.List;


@Configuration
@EnableScheduling
public class ScheduledTasks {
    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    public UserController userController;

    @Autowired
    private BonusesRepository bonusRepository;

    public int abc = 0;
    @PostConstruct
    public void scheduleTasks() {
        taskScheduler.scheduleAtFixedRate(this::updateScheduledTasks, Duration.ofMinutes(60));
    }
    private void updateScheduledTasks() {
        if(abc==0){
            abc++;
            return;
        }
        List<Bonuses> bonuses = bonusRepository.findAll();
        for (Bonuses bonus : bonuses) {
            bonus.setLeft_hours(bonus.getLeft_hours()-1);
            if(bonus.getLeft_hours()==0){
                bonus.setLeft_hours(bonus.getEvery_hours());
            }
            else{
                bonusRepository.save(bonus);
                continue;
            }
            bonusRepository.save(bonus);
            double toadd = bonus.getAmount();
            String type = bonus.getName();
            userController.updateBalanceForAllUsers(toadd, type);
        }
    }

}