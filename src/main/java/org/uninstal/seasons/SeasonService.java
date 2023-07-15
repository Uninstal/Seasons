package org.uninstal.seasons;

import org.bukkit.block.Block;
import org.uninstal.seasons.data.cleaner.SeasonDataCleaner;

import java.util.ArrayList;
import java.util.List;

public class SeasonService {
    
    private final Seasons plugin;
    private final SeasonDataCleaner cleaner;
    
    private final List<Block> chests = new ArrayList<>();
    
    public SeasonService(Seasons plugin) {
        this.plugin = plugin;
        this.cleaner = new SeasonDataCleaner(plugin);
    }

    public SeasonDataCleaner getCleaner() {
        return cleaner;
    }

    public Seasons getPlugin() {
        return plugin;
    }
    
    public void registerChest(Block block) {
        this.chests.add(block);
    }

    public List<Block> getChests() {
        return chests;
    }
}