package org.uninstal.seasons.database;

import org.uninstal.seasons.SeasonConfig;
import org.uninstal.seasons.Seasons;
import org.uninstal.seasons.data.SeasonUser;

import java.util.*;
import java.util.concurrent.CompletableFuture;

// Класс для кэширования лучших игроков
public class BestPlayersHolder {

    private final static List<UserParameter> CACHE_PARAMETERS = Arrays.asList(
      UserParameter.MOB_KILLS,
      UserParameter.PLAYER_KILLS,
      UserParameter.PLAY_TIME
    );
    private final static long RATE = SeasonConfig.getRatingUpdateRate() * 1000L;
    private final static int CACHE_SIZE = SeasonConfig.getRatingQuantity();

    private long updated = System.currentTimeMillis();
    private final Map<UserParameter, BestPlayersList> cached = new HashMap<>();
    private final Seasons plugin;

    public BestPlayersHolder(Seasons plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        for (UserParameter parameter : CACHE_PARAMETERS) {
            CompletableFuture<BestPlayersList> future = plugin.getDatabase()
              .getBestPlayers(parameter, CACHE_SIZE);
            // Используем join() для синхронной подгрузки из бд при старте сервера
            cached.put(parameter, future.join());
            
            plugin.getLogger().info(String.format("Cached «%s» placeholder",
              parameter.getPlaceholderId()));
        }
    }

    public void update() {
        // Сохраняем в бд текущих игроков на сервере, чтобы обновить данные
        Collection<SeasonUser> list = plugin.getServices().getUserService().getList();
        CompletableFuture<Void> future = plugin.getDatabase().saveUsers(list, true);
        
        // Выполняем обновление данных на сервере при завершении сохранения
        future.thenAccept(unused -> {
            for (UserParameter parameter : CACHE_PARAMETERS) {
                plugin.getDatabase().getBestPlayers(parameter, CACHE_SIZE)
                  .thenAccept(bestPlayersList -> {
                      synchronized (BestPlayersHolder.this) {
                          cached.put(parameter, bestPlayersList);
                          plugin.getLogger().info(String.format("Updated «%s» rating data",
                            parameter.getPlaceholderId()));
                      }
                  });
            }
        });
    }

    public synchronized SeasonUser get(UserParameter parameter, int place) {
        BestPlayersList list = cached.get(parameter);

        if (System.currentTimeMillis() - updated > RATE) {
            updated = System.currentTimeMillis();
            update();
        }

        List<SeasonUser> users = list.getUsers();
        return users.size() < place ? null : users.get(place - 1);
    }
}