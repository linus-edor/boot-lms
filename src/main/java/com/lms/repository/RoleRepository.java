package com.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.models.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>{

}
