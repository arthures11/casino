package com.bryja.casino.classes;

import com.bryja.casino.utils.ValidEmail;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import jakarta.persistence.*;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

    @Entity
    public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id")Long id;
        //  @Column(name = "user_id")public Long id2;
        public String name;
        @Column(unique=true)
        @NotNull
        @NotEmpty
        @ValidEmail
        public String email;
        public String password;
        public int raporty=0;

        public int balance;

        @ManyToMany
        @JoinTable(
                name = "users_roles",
                joinColumns = @JoinColumn(
                        name = "user_id", referencedColumnName = "id"),
                inverseJoinColumns = @JoinColumn(
                        name = "role_id", referencedColumnName = "id"))
        @JsonIgnore
        private Collection<Role> roles;

        @OneToMany(targetEntity=Notification.class,cascade = CascadeType.ALL,
                fetch = FetchType.LAZY, mappedBy = "user")
        public List<Notification> notyfikacje = new ArrayList<Notification>();

        public User(){
        }

        public User(String email, String name) {
            this.email = email;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
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

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getRaporty() {
            return raporty;
        }

        public void setRaporty(int raporty) {
            this.raporty = raporty;
        }

        public Collection<Role> getRoles() {
            return roles;
        }

        public void setRoles(Collection<Role> roles) {
            this.roles = roles;
        }

        public List<Notification> getNotyfikacje() {
            return notyfikacje;
        }

        public void setNotyfikacje(List<Notification> notyfikacje) {
            this.notyfikacje = notyfikacje;
        }
    }
