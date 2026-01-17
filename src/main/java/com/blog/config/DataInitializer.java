package com.blog.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.blog.entities.Role;
import com.blog.entities.User;
import com.blog.repositories.RoleRepo;
import com.blog.repositories.UserRepo;

@Configuration
public class DataInitializer {

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {

            // 1️⃣ Create ROLE_USER if not exists
            Role userRole = roleRepo.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepo.save(new Role("ROLE_USER")));

            // 2️⃣ Create ROLE_ADMIN if not exists
            Role adminRole = roleRepo.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepo.save(new Role("ROLE_ADMIN")));

            // 3️⃣ Create FIRST ADMIN USER if not exists
            String adminEmail = "admin@blog.com";

            if (userRepo.findByEmail(adminEmail).isEmpty()) {

                User admin = new User();
                admin.setName("Super Admin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("admin@123"));
                admin.setAbout("System Administrator");

                // assign roles
                admin.getRoles().add(userRole);
                admin.getRoles().add(adminRole);

                userRepo.save(admin);

                System.out.println("✅ First ADMIN user created successfully");
            } else {
                System.out.println("ℹ️ Admin user already exists");
            }
        };
    }
}