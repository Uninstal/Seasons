package org.uninstal.seasons.database;

import org.uninstal.seasons.data.SeasonUser;

import java.util.Collections;
import java.util.List;

public class BestPlayersList {
    
    private final long created = System.currentTimeMillis();
    private final UserParameter parameter;
    private final List<SeasonUser> users;

    public BestPlayersList(UserParameter parameter, List<SeasonUser> users) {
        this.parameter = parameter;
        this.users = Collections.unmodifiableList(users);
    }

    public UserParameter getParameter() {
        return parameter;
    }

    public List<SeasonUser> getUsers() {
        return users;
    }

    public long getCreated() {
        return created;
    }
}