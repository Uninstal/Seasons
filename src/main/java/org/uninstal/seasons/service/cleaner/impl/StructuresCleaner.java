package org.uninstal.seasons.service.cleaner.impl;

import org.bukkit.Bukkit;
import org.uninstal.seasons.service.cleaner.SeasonDataCleanable;
import org.uninstal.seasons.service.cleaner.SeasonDataCleaner;
import org.uninstal.structures.Main;
import org.uninstal.structures.data.Structure;

public class StructuresCleaner implements SeasonDataCleanable {
    
    @Override
    public boolean clean(SeasonDataCleaner cleaner) {
        if (!Bukkit.getPluginManager().isPluginEnabled("ApocalypseStructures")) {
            cleaner.getLogger().severe("Plugin is not enabled");
            return false;
        }
        
        Main.getInstance().getStructures()
          .forEach(Structure::crushWithTime);
        return true;
    }
}
