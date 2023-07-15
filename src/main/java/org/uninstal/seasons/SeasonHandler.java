package org.uninstal.seasons;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.uninstal.seasons.data.SeasonUser;

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
        plugin.getDatabase().getUser(e.getPlayer().getName())
          .thenAccept(plugin.getServices().getUserService()::add);
    }
    
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        SeasonUser user = plugin.getServices().getUserService().remove(e.getPlayer());
        if (user == null) {
            plugin.getLogger().severe("User is null on quit!?");
        } else {
            plugin.getDatabase().saveUser(user);
        }
    }
}