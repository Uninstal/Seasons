package org.uninstal.seasons.integration;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class Placeholder extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "seasons";
    }

    @Override
    public String getAuthor() {
        return "Uninstal";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        // будет реализовано
        return "";
    }
}