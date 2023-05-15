package com.bryja.casino.repository;

import com.bryja.casino.classes.Role;

import java.util.Collection;
import java.util.stream.Collectors;

public interface UserProjection {
    Long getId();
    String getName();
    String getEmail();
    double getBalance();
    Collection<String> getRoleNames();

}
