package org.edu.service.handlers;

import org.edu.entity.AppUser;

public interface JournalHandler {
    public void setJournalingState(AppUser appUser);

    public void removeJournalingState(AppUser appUser);

    public void setAddJournalState(AppUser appUser);

    public String processJournaling(String string, Long id);
}
