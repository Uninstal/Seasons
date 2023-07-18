package org.uninstal.seasons.service.cleaner;

import org.bukkit.Bukkit;
import org.uninstal.seasons.SeasonConfig;
import org.uninstal.seasons.SeasonDatabase;
import org.uninstal.seasons.Seasons;
import org.uninstal.seasons.data.SeasonUser;
import org.uninstal.seasons.service.cleaner.impl.CapturesCleaner;
import org.uninstal.seasons.service.cleaner.impl.ChestsCleaner;
import org.uninstal.seasons.service.cleaner.impl.ClansCleaner;
import org.uninstal.seasons.service.cleaner.impl.LevelsCleaner;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class SeasonDataCleaner {

    // Списоков параметров, по которым будут подводиться итоги
    private final static List<SeasonDatabase.TargetParameter> parameters = Arrays.asList(
      SeasonDatabase.TargetParameter.MOB_KILLS,
      SeasonDatabase.TargetParameter.PLAYER_KILLS,
      SeasonDatabase.TargetParameter.PLAY_TIME
    );

    private final Seasons plugin;
    private final Logger logger;
    private final Map<String, Class<? extends SeasonDataCleanable>> cleanableMap = new HashMap<>();
    private boolean running = false;

    public SeasonDataCleaner(Seasons plugin) {
        this.plugin = plugin;
        this.logger = new Logger(plugin);
        this.setupCleaners();
    }

    private void setupCleaners() {
        register("ApocalypseCaptures", CapturesCleaner.class);
        register("ApocalypseClans", ClansCleaner.class);
        register("ApocalypseLevels", LevelsCleaner.class);
        register("Minecraft - Chests", ChestsCleaner.class);
    }

    public void register(String dataName, Class<? extends SeasonDataCleanable> cleanable) {
        this.cleanableMap.put(dataName, cleanable);
    }

    public void clean() {
        if (running) return;
        else running = true;

        runClearServer(); // Кик игроков
        runSumming(); // Подведение итогов, выдача наград
        runCleanProcess(); // Предварительная очистка данных
        hookScript(); // Хук скрипта пост-очистки данных

        logger.info("Shutdowning server...");
        // Отключаем сервер после предварительной очистки данных
        Bukkit.shutdown();
    }

    private void runClearServer() {
        // Для начала освободим сервер от игроков
        logger.setCurrentPrefix("Cleaner > ");
        logger.info("Enabling whitelist...");
        Bukkit.setWhitelist(true);
        logger.info("Kicking all online players...");
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("§cПроводится очистка"));
    }

    private void runSumming() {
        // Подведение итогов
        // Используется сразу метод get(), так как лаги не страшны - сервер закрыт
        logger.info("The summing up of the players begins...");
        List<SeasonDatabase.BestPlayersList> lists = parameters.stream()
          .map(parameter -> {
              try {
                  return plugin.getDatabase().getBestPlayers(parameter, 3).get();
              } catch (InterruptedException | ExecutionException e) {
                  e.printStackTrace();
                  return new SeasonDatabase.BestPlayersList(parameter, Collections.emptyList());
              }
          }).collect(Collectors.toList());

        // Выдача наград в зависимости от места в топе
        for (SeasonDatabase.BestPlayersList list : lists) {
            SeasonDatabase.TargetParameter parameter = list.getParameter();
            List<SeasonUser> users = list.getUsers();

            for (int placeIndex = 0; placeIndex < users.size(); placeIndex++) {
                SeasonUser target = users.get(placeIndex);
                SeasonConfig.getRewardCommands(parameter, placeIndex + 1)
                  .forEach(command -> {
                      command = command.replace("<player>", target.getUserName());
                      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                  });
            }
        }
    }

    private void runCleanProcess() {
        logger.info("Starting clean process...");
        // Начинаем процесс очистки
        cleanableMap.forEach((id, cleaner) -> {
            logger.setCurrentPrefix("Cleaner > " + id + " | ");

            try {
                SeasonDataCleanable cleanable = cleaner.newInstance();
                cleanable.clean(this);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void hookScript() {
        logger.setCurrentPrefix("Cleaner > ");
        logger.info("Hooking post-cleaner script...");
        // Добавляем хук на запуск процесса пост-очистки данных
        Runtime.getRuntime().addShutdownHook(
          new Thread(() -> {
              try {
                  Process process = new ProcessBuilder().start();
              } catch (IOException e) {
                  throw new RuntimeException(e);
              }
          }));
    }

    public boolean isRunning() {
        return running;
    }

    public Seasons getPlugin() {
        return plugin;
    }

    public Logger getLogger() {
        return logger;
    }

    public static class Logger {

        private final java.util.logging.Logger logger;
        private String currentPrefix;

        public Logger(Seasons seasons) {
            this.logger = seasons.getLogger();
            this.currentPrefix = "";
        }

        void setCurrentPrefix(String currentPrefix) {
            this.currentPrefix = currentPrefix;
        }

        public void info(String message) {
            logger.info(currentPrefix + message);
        }

        public void warning(String message) {
            logger.warning(currentPrefix + message);
        }

        public void severe(String message) {
            logger.severe(currentPrefix + message);
        }
    }
}