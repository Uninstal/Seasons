package org.uninstal.seasons.service;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.uninstal.seasons.Seasons;

import java.util.ArrayList;
import java.util.List;

public class SeasonChestService {
    
    private final Seasons plugin;
    private final List<Block> chests = new ArrayList<>();

    public SeasonChestService(Seasons plugin) {
        this.plugin = plugin;
    }

    public Seasons getPlugin() {
        return plugin;
    }

    public void add(Block chest) {
        if (chest.getState() instanceof Chest) {
            this.chests.add(chest);
        }    
    }
    
    public void remove(Block chest) {
        this.chests.remove(chest);
    }
    
    public List<Block> getList() {
        return new ArrayList<>(chests);
    }
}