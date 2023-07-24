package org.uninstal.seasons.database;

import org.uninstal.seasons.SeasonConfig;
import org.uninstal.seasons.Seasons;
import org.uninstal.seasons.data.SeasonUser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

// Класс для кэширования лучших игроков
public class BestPlayersHolder {

    private final static List<UserParameter> CACHE_PARAMETERS = Arrays.asList(
      UserParameter.MOB_KILLS, 
      UserParameter.PLAYER_KILLS, 
      UserParameter.PLAY_TIME
    );
    private final static long RATE = SeasonConfig.getRatingUpdateRate() * 1000L;
    private final static int CACHE_SIZE = SeasonConfig.getRatingQuantity();

    private final Map<UserParameter, BestPlayersList> cached = new HashMap<>();
    private final Seasons plugin;

    public BestPlayersHolder(Seasons plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        for (UserParameter parameter : CACHE_PARAMETERS) {
            try {
                cached.put(parameter,
                  plugin.getDatabase()
                    .getBestPlayers(parameter, CACHE_SIZE)
                    .get()
                );
                plugin.getLogger().info(String.format("Cached «%s» placeholder",
                  parameter.getPlaceholderId()));
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void update() {
        for (UserParameter parameter : UserParameter.values()) {
            plugin.getDatabase().getBestPlayers(parameter, CACHE_SIZE)
              .thenAccept(bestPlayersList -> {
                  synchronized (BestPlayersHolder.this) {
                      cached.replace(parameter, bestPlayersList);
                      plugin.getLogger().info(String.format("Updated «%s» rating data",
                        parameter.getPlaceholderId()));
                  }
              });
        }
    }

    public synchronized SeasonUser get(UserParameter parameter, int place) {
        BestPlayersList list = cached.get(parameter);

        if (System.currentTimeMillis() - list.getCreated() > RATE) {
            update();
        }

        List<SeasonUser> users = list.getUsers();
        return users.size() < place ? null : users.get(place - 1);
    }
}