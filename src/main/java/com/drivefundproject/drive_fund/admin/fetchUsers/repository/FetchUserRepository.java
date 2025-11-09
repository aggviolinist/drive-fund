package com.drivefundproject.drive_fund.admin.fetchUsers.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.drivefundproject.drive_fund.auth.model.User;

public interface FetchUserRepository extends JpaRepository<User, Long> {
}
