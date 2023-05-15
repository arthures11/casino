package com.bryja.casino.classes;

import jakarta.persistence.*;

@Entity
public class Bonuses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id")Long id;

    String name;

    double amount;

    int every_hours;



    public Bonuses(String name, double amount, int every_hours) {
        this.name = name;
        this.amount = amount;
        this.every_hours = every_hours;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Bonuses() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getEvery_hours() {
        return every_hours;
    }

    public void setEvery_hours(int every_hours) {
        this.every_hours = every_hours;
    }

}
