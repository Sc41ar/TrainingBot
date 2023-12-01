package org.edu.dao;

import org.edu.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserDao extends JpaRepository<AppUser, Long> {
    //реализация за спрингом
    AppUser findAppUserByTelegramUserId(Long id);
}
