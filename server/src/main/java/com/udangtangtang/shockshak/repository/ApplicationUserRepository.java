package com.udangtangtang.shockshak.repository;

import com.udangtangtang.shockshak.domain.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, String> {
}