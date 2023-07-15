package org.uninstal.seasons;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.uninstal.seasons.command.SeasonCommand;
import org.uninstal.seasons.data.SeasonRank;
import org.uninstal.seasons.service.SeasonServices;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Seasons extends JavaPlugin {

    private final Map<String, SeasonCommand> commandMap = new HashMap<>();
    private SeasonDatabase database;
    private SeasonServices service;
    private SeasonResetTask task;

    @Override
    public void onEnable() {
        // Все данные будут в бущущем загружаться из конфига
        database = new SeasonDatabase(this, null, null, null, null);
        service = new SeasonServices(this);
        Bukkit.getServicesManager().register(SeasonServices.class, service, this, ServicePriority.Normal);
        
        task = new SeasonResetTask(this, 0L);
        task.start();

        loadRanks();

        setupEvents();
        setupCommands();
    }

    @Override
    public void onDisable() {

    }

    public SeasonServices getServices() {
        return service;
    }

    public SeasonDatabase getDatabase() {
        return database;
    }

    public SeasonResetTask getTask() {
        return task;
    }

    private void loadRanks() {
        String previousId = null;
        Iterator<String> ranks = getConfig()
          .getConfigurationSection("ranks")
          .getKeys(false).iterator();
        
        while (ranks.hasNext()) {
            String currentId = ranks.next();
            
            if (previousId == null) {
                if (!ranks.hasNext()) {
                    String display = getConfig().getString("ranks." + currentId + ".display");
                    int expTotal = getConfig().getInt("ranks." + currentId + ".exp");
                    service.getRankService().add(new SeasonRank(display, expTotal, Integer.MAX_VALUE));
                    return;
                }
                
                previousId = currentId;
                continue;
            }

            String display = getConfig().getString("ranks." + previousId + ".display");
            int expTotal = getConfig().getInt("ranks." + previousId + ".exp");
            int expTotalNext = getConfig().getInt("ranks." + currentId + ".exp");
            service.getRankService().add(new SeasonRank(display, expTotal, expTotalNext));
            
            if (!ranks.hasNext()) {
                display = getConfig().getString("ranks." + currentId + ".display");
                service.getRankService().add(new SeasonRank(display, expTotalNext, Integer.MAX_VALUE));
            }
            
            previousId = currentId;
        }
    }

    private void setupEvents() {
        Bukkit.getPluginManager().registerEvents(new SeasonHandler(this), this);
    }

    private void setupCommands() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
}