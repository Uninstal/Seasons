package org.uninstal.seasons;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class SeasonConfig {
    
    private static FileConfiguration config;

    static void setConfig(FileConfiguration config) {
        SeasonConfig.config = config;
    }
    
    public static List<String> getRewardCommands(SeasonDatabase.TargetParameter parameter, int place) {
        return config.getStringList("rewards." + parameter.getConfigId() + ".places." + place);
    }
    
    public static String getRankDisplay(String rankId) {
        return config.getString("ranks." + rankId + ".display");
    }
    
    public static int getRankExp(String rankId) {
        return config.getInt("ranks." + rankId + ".exp");
    }

    public static FileConfiguration getConfig() {
        return config;
    }
}