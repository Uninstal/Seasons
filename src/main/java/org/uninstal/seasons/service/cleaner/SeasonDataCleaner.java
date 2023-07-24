package org.uninstal.seasons.service.cleaner;

import com.uninstal.clans.datas.Clan;
import com.uninstal.clans.datas.User;
import com.uninstal.clans.manager.Manager;
import org.bukkit.Bukkit;
import org.uninstal.discordbot.DiscordBot;
import org.uninstal.discordbot.DiscordChannel;
import org.uninstal.seasons.SeasonConfig;
import org.uninstal.seasons.Seasons;
import org.uninstal.seasons.data.SeasonUser;
import org.uninstal.seasons.database.BestPlayersList;
import org.uninstal.seasons.database.UserParameter;
import org.uninstal.seasons.service.cleaner.impl.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SeasonDataCleaner {

    // Списоков параметров, по которым будут подводиться итоги
    private final static List<UserParameter> SUMMING_PARAMETERS = Arrays.asList(
      UserParameter.MOB_KILLS,
      UserParameter.PLAYER_KILLS,
      UserParameter.PLAY_TIME
    );

    private final Seasons plugin;
    private final Logger logger;
    private final Map<String, Class<? extends SeasonDataCleanable>> cleanableMap = new HashMap<>();
    private boolean running = false;

    public SeasonDataCleaner(Seasons plugin) {
        this.plugin = plugin;
        this.logger = new Logger(plugin);
        this.logger.setCurrentPrefix("Cleaner > ");
        this.setupCleaners();
    }

    private void setupCleaners() {
        register("ApocalypseCaptures", CapturesCleaner.class);
        register("ApocalypseClans", ClansCleaner.class);
        register("ApocalypseLevels", LevelsCleaner.class);
        register("ApocalypseFriends", FriendsCleaner.class);
        register("ApocalypseAuction", AuctionCleaner.class);
        register("ApocalypseStructures", StructuresCleaner.class);
        register("ApocalypseSeasons", SeasonsCleaner.class);
        register("Minecraft - Chests", ChestsCleaner.class);
        register("WorldGuard - Regions", RegionsCleaner.class);
        register("Minepacks", MinepacksCleaner.class);
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
        logger.info("Enabling whitelist...");
        Bukkit.setWhitelist(true);
        logger.info("Kicking all online players...");
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("§cПроводится очистка"));
    }

    private void runSumming() {
        // Подведение итогов
        // Используется сразу метод join(), так как лаги не страшны - сервер закрыт
        logger.info("The summing up of the players begins...");
        List<BestPlayersList> lists = SUMMING_PARAMETERS.stream()
          .map(parameter -> plugin.getDatabase()
            .getBestPlayers(parameter, SeasonConfig.getRewardsQuantity(parameter))
            .join())
          .collect(Collectors.toList());

        // Выдача наград в зависимости от места в топе игроков
        for (BestPlayersList list : lists) {
            UserParameter parameter = list.getParameter();
            List<SeasonUser> users = list.getUsers();
            if (users.size() == 0) continue;

            StringBuilder builder = new StringBuilder();
            String header = SeasonConfig.getDiscordHeader(parameter);
            String element = SeasonConfig.getDiscordElement(parameter);

            builder.append(header).append("\n");
            for (int placeIndex = 0; placeIndex < users.size(); placeIndex++) {
                SeasonUser target = users.get(placeIndex);
                SeasonConfig.getRewardsCommands(parameter, placeIndex + 1)
                  .forEach(command -> {
                      command = command.replace("<player>", target.getUserName());
                      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                  });

                builder.append(
                  element
                    .replace("<place>", String.valueOf(placeIndex + 1))
                    .replace("<value>", String.valueOf(target.getValue(parameter)))
                    .replace("<player>", target.getUserName())
                );
                if (placeIndex + 1 != users.size()) {
                    builder.append("\n");
                }
            }

            DiscordBot.getService().sendMessage(DiscordChannel.RADIO, builder.toString());
        }

        // Выдача наград в зависимости от места в топе группировок
        List<Clan> clans = Manager.getClans();
        clans.sort(Comparator.comparingInt(Clan::getRating));
        StringBuilder builder = new StringBuilder();
        builder.append(SeasonConfig.getDiscordHeader(UserParameter.CLAN)).append("\n");
        int limit = Math.min(clans.size(), SeasonConfig.getRewardsQuantity(UserParameter.CLAN));

        for (int i = 0; i < limit; i++) {
            Clan clan = clans.get(i);
            builder.append(
              SeasonConfig.getDiscordElement(UserParameter.CLAN)
              .replace("<place>", String.valueOf(i + 1))
              .replace("<clan>", clan.getName())
            );
            if (i + 1 != limit) {
                builder.append("\n");
            }

            for (User user : clan.getAllUsers()) {
                SeasonConfig.getRewardsCommands(UserParameter.CLAN, i + 1)
                  .forEach(command -> {
                      command = command.replace("<player>", user.getName());
                      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                  });
            }
        }

        DiscordBot.getService().sendMessage(DiscordChannel.RADIO, builder.toString().replaceAll("&.|§.", ""));
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
                  String command = SeasonConfig.getCleanerCommand().replace(
                    "<path>", SeasonConfig.getCleanerScript());
                  Runtime.getRuntime().exec(command);
              } catch (IOException e) {
                  e.printStackTrace();
              }
          })
        );
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