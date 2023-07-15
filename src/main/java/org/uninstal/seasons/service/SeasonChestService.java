package org.uninstal.seasons.service;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;

import java.util.ArrayList;
import java.util.List;

public class SeasonChestService {
    
    private final List<Block> chests = new ArrayList<>();
    
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