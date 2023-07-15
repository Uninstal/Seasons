package org.uninstal.seasons.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.uninstal.seasons.service.SeasonServices;

public class SeasonUser {

    private final SeasonServices service;
    private final String userName;

    private SeasonRank rank;
    private int exp;
    private int mobKills;
    private int playerKills;
    private int playTime;

    public SeasonUser(SeasonServices service, String userName, int exp) {
        this.service = service;
        this.userName = userName;
        this.setExp(exp);
    }

    public SeasonUser(SeasonServices service, String userName, int exp, int mobKills, int playerKills, int playTime) {
        this.service = service;
        this.userName = userName;
        this.mobKills = mobKills;
        this.playerKills = playerKills;
        this.playTime = playTime;
        this.setExp(exp);
    }

    public int getMobKills() {
        return mobKills;
    }

    public void setMobKills(int mobKills) {
        this.mobKills = mobKills;
    }

    public int getPlayerKills() {
        return playerKills;
    }

    public void setPlayerKills(int playerKills) {
        this.playerKills = playerKills;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }

    public String getUserName() {
        return userName;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(userName);
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;

        service.getRankService()
          .getByExp(this.exp)
          .ifPresent(rank -> this.rank = rank);
    }

    public void addExp(int exp) {
        setExp(this.exp + exp);
    }

    public SeasonRank getRank() {
        return rank;
    }

    public void setRank(SeasonRank rank) {
        this.rank = rank;
        this.exp = rank.getExpTotal();
    }

    @Override
    public String toString() {
        return "SeasonUser{" +
          "rank=" + rank +
          ", exp=" + exp +
          '}';
    }
}