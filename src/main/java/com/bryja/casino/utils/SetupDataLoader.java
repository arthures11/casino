package com.bryja.casino.utils;


import com.bryja.casino.classes.Bonuses;
import com.bryja.casino.classes.Privilege;
import com.bryja.casino.classes.Role;
import com.bryja.casino.repository.BonusesRepository;
import com.bryja.casino.repository.PrivilegeRepository;
import com.bryja.casino.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class SetupDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;


    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BonusesRepository bonusesRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;



    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;
        Privilege readPrivilege
                = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege
                = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        List<Privilege> adminPrivileges = Arrays.asList(
                readPrivilege, writePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_MOD", Arrays.asList(readPrivilege));
        createRoleIfNotFound("ROLE_USER", Arrays.asList(readPrivilege));
        createBonusIfNotFound("daily", 5, 24);
        createBonusIfNotFound("rain", 10, 1);
        createBonusIfNotFound("3h", 0.15, 3);
        createBonusIfNotFound("weekly", 100, 168);
        createBonusIfNotFound("monthly", 1000, 720);

        // Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        //  User user = new User();
        //user.setPassword(passwordEncoder.encode("test"));
        //user.setEmail("test@test.com");
        //user.setRoles(Arrays.asList(adminRole));
        // userRepository.save(user);

        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {

        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(
            String name, Collection<Privilege> privileges) {

        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }
    @Transactional
    Bonuses createBonusIfNotFound(
            String name, double amount, int hours) {

        Bonuses bonus = bonusesRepository.findByName(name);
        if (bonus == null) {
            bonus = new Bonuses(name, amount, hours, hours);
            bonusesRepository.save(bonus);
        }
        return bonus;
    }
}

