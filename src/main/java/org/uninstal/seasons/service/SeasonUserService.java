package org.uninstal.seasons.service;

import org.uninstal.seasons.Seasons;
import org.uninstal.seasons.data.SeasonUser;

import java.util.*;

public class SeasonUserService {

    private final Seasons plugin;
    private final Map<String, SeasonUser> cached = new HashMap<>();

    public SeasonUserService(Seasons plugin) {
        this.plugin = plugin;
    }

    public Seasons getPlugin() {
        return plugin;
    }

    public void load(String player) {
        plugin.getDatabase().getUser(player)
          .thenAccept(user ->
            cached.put(player, user == null
              ? new SeasonUser(plugin.getServices(), player)
              : user)
          );
    }

    public void unload(String player) {
        if (cached.containsKey(player)) {
            plugin.getDatabase().saveUser(cached.remove(player), true);
        }
    }

    public Optional<SeasonUser> get(String player) {
        return Optional.ofNullable(this.cached.get(player));
    }

    public Collection<SeasonUser> getList() {
        return new ArrayList<>(cached.values());
    }
    
    public void close() {
        for (SeasonUser user : cached.values()) {
            plugin.getDatabase().saveUser(user, false);
        }
    }
}