package org.uninstal.seasons;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.SpigotConfig;
import org.uninstal.seasons.command.*;
import org.uninstal.seasons.data.Season;
import org.uninstal.seasons.database.SeasonDatabase;
import org.uninstal.seasons.integration.Placeholder;
import org.uninstal.seasons.service.SeasonServices;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Seasons extends JavaPlugin {

    private final Map<String, SeasonCommand> commandMap = new HashMap<>();
    
    private SeasonDatabase database;
    private SeasonServices services;
    private SeasonResetTask task;
    private Season currentSeason;
    
    @Override
    public void onEnable() {

        // Подгрузка конфига
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        SeasonConfig.setPluginConfig(getConfig());

        if (!new File(SeasonConfig.getCleanerScript()).exists()) {
            getLogger().warning("Cleaner script is not defined");
        }

        try {
            // Загрузка данных о сезоне
            currentSeason = new Season(this);
            currentSeason.initialize();

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
        try {
            this.currentSeason.save();
            this.services.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Season getCurrentSeason() {
        return currentSeason;
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
        database.getBestPlayersHolder().initialize();
        task = new SeasonResetTask(this, currentSeason);
        task.start();

        Bukkit.getServicesManager().register(SeasonServices.class, services, this, ServicePriority.Normal);
    }

    private void setupEvents() {
        Bukkit.getPluginManager().registerEvents(new SeasonHandler(this), this);
    }

    private void setupCommands() {
        commandMap.put("add", new SeasonAddCommand(this));
        commandMap.put("set", new SeasonSetCommand(this));
        commandMap.put("check", new SeasonCheckCommand(this));
        commandMap.put("update", new SeasonUpdateCommand(this));
        commandMap.put("clean", new SeasonCleanCommand(this));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(SpigotConfig.unknownCommandMessage);
            return false;
        }

        if (args.length == 0) {
            sendHelpMessage(sender);
            return false;
        }

        SeasonCommand seasonCommand = commandMap.get(args[0]);
        if (seasonCommand == null || seasonCommand.getArgs() > args.length) {
            sendHelpMessage(sender);
        } else {
            seasonCommand.run(sender, args);
        }

        return false;
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("§7[§6Сезоны§7] /season add exp|mobs|players <ник> <значение>");
        sender.sendMessage("§7[§6Сезоны§7] /season set exp|mobs|players <ник> <значение>");
        sender.sendMessage("§7[§6Сезоны§7] /season check <ник>");
        sender.sendMessage("§7[§6Сезоны§7] /season update");
        sender.sendMessage("§7[§6Сезоны§7] /season clean §8(CONSOLE)");
    }
}