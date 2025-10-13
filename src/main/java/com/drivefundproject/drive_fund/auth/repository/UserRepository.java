package com.drivefundproject.drive_fund.auth.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.drivefundproject.drive_fund.auth.model.User;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
     Optional<User> findByEmail(String email);
}
