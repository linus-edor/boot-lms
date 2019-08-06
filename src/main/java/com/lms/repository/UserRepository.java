package com.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lms.models.User;

public interface UserRepository extends JpaRepository<User, Long>{
//  @Query("SELECT u from User u where u.username = :username")
//  User findByUsername(@Param("username") String username);
	User findByUsername(String username);
}
