package org.edu.dao;

import org.edu.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AppUserDao extends JpaRepository<AppUser, Long>,
        JpaSpecificationExecutor<AppUser> {
    //реализация за спрингом
    AppUser findAppUserByTelegramUserId(Long id);

    AppUser findByFirstName(String firstName);

    AppUser findByFirstNameAndLastName(String firstName, String lastName);

    Optional<AppUser> findByUsername(String username);
}
