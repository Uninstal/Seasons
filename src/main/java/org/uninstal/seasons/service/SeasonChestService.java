package org.uninstal.seasons.service;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.uninstal.seasons.Seasons;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
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

    void initialize() {
        try {
            File chestsFile = new File(plugin.getDataFolder(), "chests.txt");
            if (!chestsFile.exists()) {
                plugin.getLogger().info(chestsFile.createNewFile() 
                  ? "Created new empty chests data file" 
                  : "Failed to create new chests file");
            } else {
                BufferedReader reader = new BufferedReader(new FileReader(chestsFile));
                String line;
                int count = 0;
                for (; (line = reader.readLine()) != null; count++) {
                    add(stringToLocation(line).getBlock());
                }
                plugin.getLogger().info(String.format("Loaded %s chest locations from file", count));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void save() {
        try {
            File chestsFile = new File(plugin.getDataFolder(), "chests.txt");
            try (BufferedWriter writer = Files.newBufferedWriter(chestsFile.toPath(),
              StandardOpenOption.TRUNCATE_EXISTING)) {
                int count = 0;
                for (Block block : chests) {
                    writer.write(locationToString(block.getLocation()));
                    count++;
                }
                plugin.getLogger().info(String.format("Saved %s chest locations to file", count));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String locationToString(Location location) {
        return location.getWorld().getName()
          + "_" + location.getBlockX()
          + "_" + location.getBlockY()
          + "_" + location.getBlockZ();
    }

    private Location stringToLocation(String string) {
        String[] args = string.split("_");
        return new Location(
          Bukkit.getWorld(args[0]),
          Integer.parseInt(args[1]),
          Integer.parseInt(args[2]),
          Integer.parseInt(args[3])
        );
    }
}