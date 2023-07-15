package org.uninstal.seasons.service;

import org.bukkit.entity.Player;
import org.uninstal.seasons.Seasons;
import org.uninstal.seasons.data.SeasonUser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SeasonUserService {

    private final Seasons plugin;
    private final Map<Player, SeasonUser> cached = new HashMap<>();

    public SeasonUserService(Seasons plugin) {
        this.plugin = plugin;
    }

    public Seasons getPlugin() {
        return plugin;
    }

    public void add(SeasonUser user) {
        this.cached.put(user.getPlayer(), user);
    }

    public Optional<SeasonUser> get(Player player) {
        return Optional.ofNullable(this.cached.get(player));
    }

    public SeasonUser remove(Player player) {
        return this.cached.remove(player);
    }

    public Collection<SeasonUser> getList() {
        return cached.values();
    }
}