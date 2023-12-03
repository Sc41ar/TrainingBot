package org.edu.dao;

import org.edu.entity.StudentSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<StudentSubscription, Long> {
    Optional<StudentSubscription> findById(Long id);
}