package org.uninstal.seasons.service.cleaner.impl;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.uninstal.seasons.service.cleaner.SeasonDataCleanable;
import org.uninstal.seasons.service.cleaner.SeasonDataCleaner;

import java.io.File;
import java.util.UUID;

@Deprecated
public class EssentialsCleaner implements SeasonDataCleanable {
    
    @Override
    public boolean clean(SeasonDataCleaner cleaner) {
        if (!Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            cleaner.getLogger().severe("Plugin is not enabled");
            return false;
        }

        cleaner.getLogger().info("Saving some datas...");
        Essentials essentials = Essentials.getPlugin(Essentials.class);
        for (UUID userId : essentials.getUserMap().getAllUniqueUsers()) {
            User user = essentials.getUser(userId);
            // тут сохранение некоторой статистики юзера
        }

        cleaner.getLogger().info("Deleting «userdata» folder...");
        File dataFoler = new File(essentials.getDataFolder(), "userdata");
        return dataFoler.delete();
    }
}