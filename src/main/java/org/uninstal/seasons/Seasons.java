package org.uninstal.seasons;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.uninstal.seasons.command.SeasonCommand;
import org.uninstal.seasons.integration.Placeholder;
import org.uninstal.seasons.service.SeasonServices;

import java.util.HashMap;
import java.util.Map;

public class Seasons extends JavaPlugin {

    private final Map<String, SeasonCommand> commandMap = new HashMap<>();
    
    private SeasonDatabase database;
    private SeasonServices services;
    private SeasonResetTask task;

    @Override
    public void onEnable() {
        
        // Подгрузка конфига
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        SeasonConfig.setConfig(getConfig());
        
        try {
            // Все данные будут в бущущем загружаться из конфига
            database = new SeasonDatabase(this,
              getConfig().getString("database.host"),
              getConfig().getString("database.base"),
              getConfig().getString("database.user"),
              getConfig().getString("database.password")
            );
            database.init();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        setupSevices();
        setupEvents();
        setupCommands();
        
        new Placeholder(this).register();
    }

    @Override
    public void onDisable() {
        
    }

    public SeasonServices getServices() {
        return services;
    }

    public SeasonDatabase getDatabase() {
        return database;
    }

    public SeasonResetTask getTask() {
        return task;
    }
    
    private void setupSevices() {
        services = new SeasonServices(this);
        services.initialize();
        task = new SeasonResetTask(this, 0L);
        task.start();
        
        Bukkit.getServicesManager().register(SeasonServices.class, services, this, ServicePriority.Normal);
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