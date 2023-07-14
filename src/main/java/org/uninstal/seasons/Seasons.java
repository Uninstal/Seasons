package org.uninstal.seasons;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.uninstal.seasons.command.SeasonCommand;

import java.util.HashMap;
import java.util.Map;

public class Seasons extends JavaPlugin {
    
    private final Map<String, SeasonCommand> commandMap = new HashMap<>();

    @Override
    public void onEnable() {
        setupEvents();
        setupCommands();
    }

    @Override
    public void onDisable() {
        
    }
    
    private void setupEvents() {
        Bukkit.getPluginManager().registerEvents(new SeasonsHandler(this), this);
    }
    
    private void setupCommands() {
        
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
}