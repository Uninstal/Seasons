package org.uninstal.seasons;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class SeasonConfig {
    
    private static YamlConfiguration config;

    static void setConfig(YamlConfiguration config) {
        SeasonConfig.config = config;
    }
    
    public static List<String> getRewardCommands(SeasonDatabase.TargetParameter parameter, int place) {
        return config.getStringList("rewards." + parameter.getConfigId() + ".places." + place);
    }
}