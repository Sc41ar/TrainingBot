package org.edu.service.handlers;

import org.edu.entity.AppUser;

public interface OccupationHandler {
    public String processOccupation(AppUser appUser);
    public String parseOccupatin(AppUser appUser, String string);
}
