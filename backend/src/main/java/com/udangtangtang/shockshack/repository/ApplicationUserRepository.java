package com.udangtangtang.shockshack.repository;


import com.udangtangtang.shockshack.domain.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, String> {
}