package org.uninstal.seasons.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.uninstal.seasons.database.UserParameter;
import org.uninstal.seasons.service.SeasonServices;
import org.uninstal.seasons.util.TimeFormat;

public class SeasonUser {

    private final SeasonServices service;
    private final String userName;
    private final long join = System.currentTimeMillis();

    private SeasonRank rank;
    private int exp;
    private int mobKills;
    private int playerKills;
    private long playTime;

    public SeasonUser(SeasonServices service, String userName) {
        this.service = service;
        this.userName = userName;
        this.mobKills = 0;
        this.playerKills = 0;
        this.playTime = 0;
        this.setExp(0);
    }

    public SeasonUser(SeasonServices service, String userName, int exp, int mobKills, int playerKills, long playTime) {
        this.service = service;
        this.userName = userName;
        this.mobKills = mobKills;
        this.playerKills = playerKills;
        this.playTime = playTime;
        this.setExp(exp);
    }

    public long getJoin() {
        return join;
    }
    
    public Object getValue(UserParameter parameter) {
        switch (parameter) {
            case MOB_KILLS:
                return mobKills;
            case PLAYER_KILLS:
                return playerKills;
            case PLAY_TIME:
                return TimeFormat.toShort(playTime);
            case EXP:
                return exp;
        }
        return new Object();
    }

    public int getMobKills() {
        return mobKills;
    }

    public void setMobKills(int mobKills) {
        this.mobKills = mobKills;
    }
    
    public void addMobKills(int mobKills) {
        this.mobKills += mobKills;
    }

    public int getPlayerKills() {
        return playerKills;
    }

    public void setPlayerKills(int playerKills) {
        this.playerKills = playerKills;
    }
    
    public void addPlayerKills(int playerKills) {
        this.playerKills += playerKills;
    }

    public long getPlayTime() {
        return playTime;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }
    
    public void addPlayTime(long playTime) {
        this.playTime += playTime;
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