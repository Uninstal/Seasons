package org.uninstal.seasons.service;

import org.uninstal.seasons.Seasons;
import org.uninstal.seasons.data.SeasonRank;

import java.util.ArrayList;
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
}