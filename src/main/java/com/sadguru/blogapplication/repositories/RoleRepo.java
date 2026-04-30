package com.sadguru.blogapplication.repositories;

import com.sadguru.blogapplication.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Integer> {
}
