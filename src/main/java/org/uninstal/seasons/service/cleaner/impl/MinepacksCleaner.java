package org.uninstal.seasons.service.cleaner.impl;

import org.bukkit.Bukkit;
import org.uninstal.seasons.database.SeasonDatabase;
import org.uninstal.seasons.service.cleaner.SeasonDataCleanable;
import org.uninstal.seasons.service.cleaner.SeasonDataCleaner;

import java.sql.PreparedStatement;

public class MinepacksCleaner implements SeasonDataCleanable {
    
    @Override
    public boolean clean(SeasonDataCleaner cleaner) {
        if (!Bukkit.getPluginManager().isPluginEnabled("Minepacks")) {
            cleaner.getLogger().severe("Plugin is not enabled");
            return false;
        }

        SeasonDatabase database = cleaner.getPlugin().getDatabase();
        cleaner.getLogger().info("Dropping tables in SQL (sync)...");
        database.execute("DROP TABLE backpacks", PreparedStatement::executeUpdate);
        database.execute("DROP TABLE backpack_players", PreparedStatement::executeUpdate);
        return true;
    }
}
