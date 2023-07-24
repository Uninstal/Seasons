package org.uninstal.seasons.data;

import org.bukkit.configuration.file.YamlConfiguration;
import org.uninstal.seasons.Seasons;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class Season {

    private final Seasons plugin;

    private Calendar start;
    private Calendar end;

    public Season(Seasons plugin) {
        this.plugin = plugin;
    }

    public Seasons getPlugin() {
        return plugin;
    }

    public Calendar getEnd() {
        return (Calendar) end.clone();
    }

    public Calendar getStart() {
        return (Calendar) start.clone();
    }

    public void initialize() {
        File file = new File(plugin.getDataFolder(), "season.yml");
        this.start = Calendar.getInstance();
        if (file.exists()) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            if (yaml.contains("start")) {
                this.start.setTimeInMillis(yaml.getLong("start"));
            }
        }
        this.end = (Calendar) start.clone();
        this.end.add(Calendar.DATE, 90);
    }
    
    public void save() throws IOException {
        File file = new File(plugin.getDataFolder(), "season.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("start", start.getTimeInMillis());
        yaml.save(file);
    }
}