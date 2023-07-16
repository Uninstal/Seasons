package org.uninstal.seasons;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SeasonHandler implements Listener {
    
    private final Seasons plugin;

    public SeasonHandler(Seasons plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent e) {
        if (plugin.getServices().getCleaner().isRunning()) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "§cВыполняется очистка данных");
        }
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.getServices().getUserService().load(e.getPlayer());
    }
    
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        plugin.getServices().getUserService().unload(e.getPlayer());
    }
}