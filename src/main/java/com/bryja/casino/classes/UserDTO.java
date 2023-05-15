package com.bryja.casino.classes;

import java.util.Collection;

public class UserDTO {

    private String name;
    private String email;
    private double balance;
    private Collection<Role> roles;

    public UserDTO(String name, String email, double balance, Collection<Role> roles) {
        this.name = name;
        this.email = email;
        this.balance = balance;
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    // getters and setters

}