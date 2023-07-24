package org.uninstal.seasons.command;

import org.bukkit.command.CommandSender;
import org.uninstal.seasons.Seasons;

public abstract class SeasonCommand {
    
    protected final Seasons plugin;
    protected final int args;

    public SeasonCommand(Seasons plugin, int args) {
        this.plugin = plugin;
        this.args = args;
    }

    public Seasons getPlugin() {
        return plugin;
    }

    public int getArgs() {
        return args;
    }
    
    public abstract void run(CommandSender sender, String[] args);
}