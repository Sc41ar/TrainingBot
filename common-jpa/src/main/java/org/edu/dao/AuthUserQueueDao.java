package org.edu.dao;

import org.edu.entity.AppUser;
import org.edu.entity.AuthUserQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AuthUserQueueDao extends JpaRepository<AuthUserQueue, Long> , JpaSpecificationExecutor<AuthUserQueue> {
    Optional<AppUser> findById(AppUser appUser);
}
