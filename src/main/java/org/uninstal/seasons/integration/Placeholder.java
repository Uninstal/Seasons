package org.uninstal.seasons.integration;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.uninstal.seasons.Seasons;
import org.uninstal.seasons.data.SeasonRank;
import org.uninstal.seasons.data.SeasonUser;

import java.util.Optional;

public class Placeholder extends PlaceholderExpansion {

    private final Seasons plugin;

    public Placeholder(Seasons plugin) {
        this.plugin = plugin;
    }

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
        if (params.equalsIgnoreCase("user_rank")) {
            Optional<SeasonUser> seasonUser = plugin.getServices().getUserService().get(p);
            return seasonUser.map(user -> user.getRank().getDisplay()).orElseGet(() -> String.valueOf(0));
        }
        if (params.equalsIgnoreCase("user_exp")) {
            Optional<SeasonUser> seasonUser = plugin.getServices().getUserService().get(p);
            return seasonUser.map(user -> String.valueOf(user.getExp())).orElseGet(() -> String.valueOf(0));
        }
        if (params.equalsIgnoreCase("user_rank_next")) {
            Optional<SeasonUser> seasonUser = plugin.getServices().getUserService().get(p);
            return seasonUser.map(user -> plugin.getServices()
                .getRankService().getByExp(user.getRank().getExpToNext())
                .map(SeasonRank::getDisplay).orElse(""))
              .orElseGet(() -> String.valueOf(100));
        }
        if (params.equalsIgnoreCase("user_exp_next")) {
            Optional<SeasonUser> seasonUser = plugin.getServices().getUserService().get(p);
            return seasonUser.map(user -> String.valueOf(user.getRank().getExpToNext())).orElseGet(() -> String.valueOf(100));
        }
        return "";
    }
}