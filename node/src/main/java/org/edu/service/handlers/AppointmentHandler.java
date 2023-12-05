package org.edu.service.handlers;

import org.edu.entity.AppUser;

public interface AppointmentHandler {
    public String processAppointment(AppUser appUser);
    public String parseAppointment(AppUser appUser, String string);
}
