package org.uninstal.seasons.service;

import org.bukkit.entity.Player;
import org.uninstal.seasons.Seasons;
import org.uninstal.seasons.data.SeasonUser;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SeasonUserService {

    private final Seasons plugin;
    private final Map<Player, CompletableFuture<Player>> loadings = new HashMap<>();
    private final Map<Player, SeasonUser> cached = new HashMap<>();

    public SeasonUserService(Seasons plugin) {
        this.plugin = plugin;
    }

    public Seasons getPlugin() {
        return plugin;
    }
    
    public void load(Player player) {
        loadings.put(player,
          plugin.getDatabase().getUser(player.getName())
            .thenApply(user -> {
                loadings.remove(player);
                if (player.isOnline()) {
                    cached.put(player, user);
                }
                return player;
            })
        );
    }
    
    public void unload(Player player) {
        if (cached.containsKey(player)) {
            plugin.getDatabase().saveUser(cached.remove(player));
        }
    }

    public Optional<SeasonUser> get(Player player) {
        return Optional.ofNullable(this.cached.get(player));
    }

    public Collection<SeasonUser> getList() {
        return cached.values();
    }
}