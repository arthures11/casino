package com.bryja.casino.controllers;

import com.bryja.casino.classes.Bonuses;
import com.bryja.casino.repository.BonusesRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;

import java.util.List;

@Configuration
@EnableScheduling
public class ScheduledTasks {

    // Autowire user service
    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    public UserController userController;

    @Autowired
    private BonusesRepository bonusRepository;

    @PostConstruct
    public void scheduleTasks() {
        List<Bonuses> bonuses = bonusRepository.findAll();
        for (Bonuses bonus : bonuses) {
            int hours = bonus.getEvery_hours();
            double toadd = bonus.getAmount();
            String type = bonus.getName();
            String cronExpression = "0 0 */" + hours + " * * *";

            taskScheduler.schedule(new UpdateBalancesTask(toadd, type), new CronTrigger(cronExpression));
        }
    }

    private class UpdateBalancesTask implements Runnable {
        private double toadd;

        private String type;

        public UpdateBalancesTask(double toadd, String type) {
            this.toadd = toadd;
            this.type = type;
        }

        @Override
        public void run() {
            userController.updateBalanceForAllUsers(toadd, type);
        }
    }
}