package org.uninstal.seasons.service.cleaner.impl;

import org.bukkit.Bukkit;
import org.uninstal.auction.Main;
import org.uninstal.seasons.service.cleaner.SeasonDataCleanable;
import org.uninstal.seasons.service.cleaner.SeasonDataCleaner;

public class AuctionCleaner implements SeasonDataCleanable {

    @Override
    public boolean clean(SeasonDataCleaner cleaner) {
        if (!Bukkit.getPluginManager().isPluginEnabled("ApocalypseAuction")) {
            cleaner.getLogger().severe("Plugin is not enabled");
            return false;
        }

        cleaner.getLogger().info("Dropping table in SQL (sync)...");
        Main.getInstance().getDatabase().dropTable();
        return true;
    }
}
