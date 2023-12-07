package org.edu.service.handlers;

import org.edu.entity.AppUser;
import org.edu.entity.Occupation;

public interface OccupationHandler {
    public String initOccupation(AppUser appUser);

    public String processOccupatin(AppUser appUser, String string);

    public Occupation parseOccupation(String string);
}
