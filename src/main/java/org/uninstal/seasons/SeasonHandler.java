package org.uninstal.seasons;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class SeasonHandler implements Listener {
    
    private final Seasons plugin;

    public SeasonHandler(Seasons plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent e) {
        if (!plugin.getService().getCleaner().isRunning()) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "§cВыполняется очистка данных");
        }
    }
}