package org.uninstal.seasons;

import org.bukkit.configuration.file.FileConfiguration;
import org.uninstal.seasons.database.UserParameter;

import java.util.List;

public class SeasonConfig {
    
    private static FileConfiguration config;

    static void setPluginConfig(FileConfiguration config) {
        SeasonConfig.config = config;
    }

    public static List<String> getRewardsCommands(UserParameter parameter, int place) {
        return config.getStringList("rewards." + parameter.getConfigId() + ".places." + place);
    }

    public static int getRewardsQuantity(UserParameter parameter) {
        return config.getInt("rewards." + parameter.getConfigId() + ".quantity");
    }
    
    public static String getDiscordHeader(UserParameter parameter) {
        return config.getString("rewards." + parameter.getConfigId() + ".discord.header");
    }

    public static String getDiscordElement(UserParameter parameter) {
        return config.getString("rewards." + parameter.getConfigId() + ".discord.element");
    }
    
    public static String getCleanerScript() {
        return config.getString("cleaner.script");
    }

    public static List<String> getCleanerRegions() {
        return config.getStringList("cleaner.regions");
    }
    
    public static String getCleanerCommand() {
        return config.getString("cleaner.command");
    }
    
    public static String getRankDisplay(String rankId) {
        return config.getString("ranks." + rankId + ".display");
    }
    
    public static int getRankExp(String rankId) {
        return config.getInt("ranks." + rankId + ".exp");
    }
    
    public static int getRatingUpdateRate() {
        return config.getInt("rating-cache.update");
    }

    public static int getRatingQuantity() {
        return config.getInt("rating-cache.quantity");
    }
    
    public static int getSeasonDuration() {
        return config.getInt("season-duration", 90);
    }

    public static FileConfiguration getConfig() {
        return config;
    }
}