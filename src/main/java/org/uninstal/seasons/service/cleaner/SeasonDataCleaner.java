package org.uninstal.seasons.service.cleaner;

import org.bukkit.Bukkit;
import org.uninstal.seasons.Seasons;
import org.uninstal.seasons.service.cleaner.impl.CapturesCleaner;
import org.uninstal.seasons.service.cleaner.impl.ChestsCleaner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SeasonDataCleaner {

    private final Seasons plugin;
    private final Logger logger;
    private final Map<String, Class<? extends SeasonDataCleanable>> cleanableMap = new HashMap<>();
    private boolean running = false;

    public SeasonDataCleaner(Seasons plugin) {
        this.plugin = plugin;
        this.logger = new Logger(plugin);

        setupCleaners();
    }

    private void setupCleaners() {
        // Возможно, будет перенесено в отдельный скрипт.
        // Нужны тесты.
        // register("Minecraft - Worlds", MinecraftCleaner.class);
        // register("Essentials", EssentialsCleaner.class);
        register("ApocalypseCaptures", CapturesCleaner.class);
        register("Minecraft - Chests", ChestsCleaner.class);
    }

    public void register(String dataName, Class<? extends SeasonDataCleanable> cleanable) {
        this.cleanableMap.put(dataName, cleanable);
    }
    
    public void clean() {
        if (running) return;
        else running = true;

        // Для начала освободим сервер от игроков
        logger.setCurrentPrefix("Cleaner > ");
        logger.info("Enabling whitelist...");
        Bukkit.setWhitelist(true);
        logger.info("Kicking all online players...");
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("§cПроводится очистка"));

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
        
        logger.info("Shutdowning server...");
        // Отключаем сервер после предварительной очистки данных
        Bukkit.shutdown();
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