package org.edu.service.handlers;

import org.edu.entity.AppUser;

public interface AuthHandler {
    public String processAuth(AppUser appUser);

    public String showUnverifiedUsersList(AppUser appUser);

    public String getUsernameFromCallBackQueryMessage(String message);

    public void setUserTeacherState(String username);

    public void returnBasicState(AppUser appUser);
}
