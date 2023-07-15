package org.uninstal.seasons.data.cleaner.impl;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.uninstal.seasons.data.cleaner.SeasonDataCleanable;
import org.uninstal.seasons.data.cleaner.SeasonDataCleaner;

import java.io.File;

@Deprecated
public class MinecraftCleaner implements SeasonDataCleanable {
    
    private SeasonDataCleaner cleaner;
    private File worldFolder;
    
    @Override
    public boolean clean(SeasonDataCleaner cleaner) {
        this.cleaner = cleaner;
        boolean worldDeleted = deletePlayerDataInWorld("world");
        boolean lobbyDeleted = deletePlayerDataInWorld("lobby");
        return worldDeleted && lobbyDeleted;
    }
    
    private boolean deletePlayerDataInWorld(String worldName) {
        cleaner.getLogger().info(String.format("Deleting data for world «%s»...", worldName));
        
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            cleaner.getLogger().warning("Cannot delete data from world, because he is null");
            return false;
        }

        this.worldFolder = world.getWorldFolder();
        return deleteFolderInWorld("advancements") 
          && deleteFolderInWorld("stats") 
          && deleteFolderInWorld("playerdata");
    }
    
    private boolean deleteFolderInWorld(String folder) {
        boolean deleted = new File(worldFolder, folder).delete();
        if (!deleted) {
            cleaner.getLogger().warning(String.format("Cannot delete folder «%s», because he is null", folder));
            return false;
        }
        return true;
    }
}