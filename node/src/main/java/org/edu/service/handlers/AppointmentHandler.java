package org.edu.service.handlers;

import org.edu.entity.AppUser;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface AppointmentHandler {
    public SendMessage processAppointment(AppUser appUser);

    public String parseAppointment(AppUser appUser, String string);

    public void saveSubsCapacity(AppUser appUser, int count);

    public SendMessage chooseSubscriptionPlan(AppUser appUser);

    public void notifyAdmin(AppUser appUser, int count);
}