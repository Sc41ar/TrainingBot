package org.edu.service;

import org.edu.entity.AppUser;

public interface CommandProcessorService {
    public String proccessServiceCommand(AppUser appUser, String cmd);
}
