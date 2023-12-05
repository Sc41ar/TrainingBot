package org.edu.service;

import org.edu.entity.AppUser;

public interface CommandProcessorService {
    public String processServiceCommand(AppUser appUser, String cmd);

    public String processCallBackQuery(AppUser appUser, String query);
}
