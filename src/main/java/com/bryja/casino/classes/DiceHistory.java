package com.bryja.casino.classes;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class DiceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id")Long id;


    public LocalDateTime timestamp;

    public double bet_amount;

    public String odds;

    public int result;

    public String profit;

    @ManyToOne(targetEntity=User.class,fetch = FetchType.LAZY)
    @JsonBackReference
    @JsonIgnore
    private User user;


    public DiceHistory(Long id, LocalDateTime timestamp, double bet_amount, String odds, int result, String profit, User user) {
        this.id = id;
        this.timestamp = timestamp;
        this.bet_amount = bet_amount;
        this.odds = odds;
        this.result = result;
        this.profit = profit;
        this.user = user;
    }
    public DiceHistory( LocalDateTime timestamp, double bet_amount, String odds, int result, String profit, User user) {
        this.timestamp = timestamp;
        this.bet_amount = bet_amount;
        this.odds = odds;
        this.result = result;
        this.profit = profit;
        this.user = user;
    }

    public DiceHistory() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getBet_amount() {
        return bet_amount;
    }

    public void setBet_amount(double bet_amount) {
        this.bet_amount = bet_amount;
    }

    public String getOdds() {
        return odds;
    }

    public void setOdds(String odds) {
        this.odds = odds;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
