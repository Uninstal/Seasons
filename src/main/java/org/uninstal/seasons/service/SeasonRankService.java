package org.uninstal.seasons.service;

import org.uninstal.seasons.SeasonConfig;
import org.uninstal.seasons.Seasons;
import org.uninstal.seasons.data.SeasonRank;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class SeasonRankService {
    
    private final Seasons plugin;
    private final List<SeasonRank> ranks = new ArrayList<>();

    public SeasonRankService(Seasons plugin) {
        this.plugin = plugin;
    }

    public Seasons getPlugin() {
        return plugin;
    }

    public void add(SeasonRank rank) {
        this.ranks.add(rank);
    }
    
    public void remove(SeasonRank rank) {
        this.ranks.remove(rank);
    }
    
    public List<SeasonRank> getList() {
        return new ArrayList<>(ranks);
    }
    
    public Optional<SeasonRank> getByExp(int exp) {
        return ranks.stream()
          .filter(rank -> rank.getExpTotal() <= exp && exp < rank.getExpToNext())
          .findFirst();
    }

    void initialize() {
        String previousId = null;
        Iterator<String> ranks = SeasonConfig.getConfig()
          .getConfigurationSection("ranks")
          .getKeys(false).iterator();

        while (ranks.hasNext()) {
            String currentId = ranks.next();

            if (previousId == null) {
                if (!ranks.hasNext()) {
                    String display = SeasonConfig.getRankDisplay(currentId);
                    int expTotal = SeasonConfig.getRankExp(currentId);
                    add(new SeasonRank(display, expTotal, Integer.MAX_VALUE));
                    return;
                }

                previousId = currentId;
                continue;
            }

            String display = SeasonConfig.getRankDisplay(previousId);
            int expTotal = SeasonConfig.getRankExp(previousId);
            int expTotalNext = SeasonConfig.getRankExp(currentId);
            add(new SeasonRank(display, expTotal, expTotalNext));

            if (!ranks.hasNext()) {
                display = SeasonConfig.getRankDisplay(currentId);
                add(new SeasonRank(display, expTotalNext, Integer.MAX_VALUE));
            }

            previousId = currentId;
        }
    }
}