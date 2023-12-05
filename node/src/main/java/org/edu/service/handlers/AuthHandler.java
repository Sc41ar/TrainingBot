package org.edu.service.handlers;

import org.edu.entity.AppUser;

public interface AuthHandler {
    public String processAuth(AppUser appUser);
    public String showUnverifiedUsersList();
}
