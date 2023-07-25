package org.uninstal.seasons.service.cleaner.impl;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.wiefferink.areashop.AreaShop;
import me.wiefferink.areashop.regions.GeneralRegion;
import me.wiefferink.areashop.regions.RentRegion;
import org.bukkit.Bukkit;
import org.uninstal.seasons.SeasonConfig;
import org.uninstal.seasons.service.cleaner.SeasonDataCleanable;
import org.uninstal.seasons.service.cleaner.SeasonDataCleaner;

import java.util.HashMap;

public class AreaShopCleaner implements SeasonDataCleanable {

    @Override
    public boolean clean(SeasonDataCleaner cleaner) {
        if (!Bukkit.getPluginManager().isPluginEnabled("AreaShop")) {
            cleaner.getLogger().severe("Plugin is not enabled");
            return false;
        }

        AreaShop plugin = AreaShop.getInstance();
        for (String regionId : SeasonConfig.getCleanerRegions()) {
            GeneralRegion generalRegion = plugin.getFileManager().getRegion(regionId);
            if (generalRegion instanceof RentRegion) {
                ((RentRegion) generalRegion).setRenter(null);
                ProtectedRegion region = generalRegion.getRegion();
                region.getMembers().clear();
                region.getOwners().clear();
                region.setFlags(new HashMap<>());
                cleaner.getLogger().info(String.format("Region with id «%s» was cleared", regionId));
            } else {
                cleaner.getLogger().info(String.format("Region with id «%s» is null or is not rentable", regionId));
            }
        }
        return true;
    }
}
