package org.edu.dao;

import org.edu.entity.AuthUserQueue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthUserQueueDao extends JpaRepository<AuthUserQueue, Long> {
}
