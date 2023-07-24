package org.uninstal.seasons.service.cleaner.impl;

import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.uninstal.seasons.service.cleaner.SeasonDataCleanable;
import org.uninstal.seasons.service.cleaner.SeasonDataCleaner;

public class ChestsCleaner implements SeasonDataCleanable {

    @Override
    public boolean clean(SeasonDataCleaner cleaner) {
        cleaner.getPlugin().getServices()
          .getChestService().getList()
          .forEach(block -> {
              Location location = block.getLocation();
              cleaner.getLogger().info(String.format("Clearing chest in %s %s %s...",
                location.getBlockX(), location.getBlockY(), location.getBlockZ()));
              Chest chest = (Chest) block.getState();
              chest.getInventory().clear();
          });
        return true;
    }
}