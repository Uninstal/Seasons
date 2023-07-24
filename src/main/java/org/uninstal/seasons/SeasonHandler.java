package org.uninstal.seasons;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.uninstal.seasons.data.SeasonUser;
import org.uninstal.seasons.service.SeasonUserService;

import java.util.Optional;

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
        plugin.getServices().getUserService()
          .load(e.getPlayer().getName());
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        if (killer == null) return;
        Optional<SeasonUser> userOptional = plugin.getServices()
          .getUserService()
          .get(killer.getName());
        
        if (userOptional.isPresent()) {
            SeasonUser user = userOptional.get();
            if (e.getEntityType() == EntityType.PLAYER) {
                user.addPlayerKills(1);
            } else {
                user.addMobKills(1);
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        SeasonUserService service = plugin.getServices().getUserService();
        String userName = e.getPlayer().getName();
        
        service.get(userName).ifPresent(user -> 
          user.addPlayTime(System.currentTimeMillis() - user.getJoin()));
        service.unload(userName);
    }
}