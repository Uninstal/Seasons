package org.uninstal.seasons.data.cleaner.impl;

import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.uninstal.seasons.data.cleaner.SeasonDataCleanable;
import org.uninstal.seasons.data.cleaner.SeasonDataCleaner;

public class ChestsCleaner implements SeasonDataCleanable {

    @Override
    public boolean clean(SeasonDataCleaner cleaner) {
        cleaner.getPlugin().getService().getChests()
          .forEach(block -> {
              Location location = block.getLocation();
              cleaner.getLogger().info(String.format("Clearing chest in %s %s %s...",
                location.getBlockX(), location.getBlockY(), location.getBlockZ()));
              Chest chest = (Chest) block.getState();
              chest.getInventory().clear();
          });
        return false;
    }
}