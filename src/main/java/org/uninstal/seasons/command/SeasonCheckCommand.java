package org.uninstal.seasons.command;

import org.bukkit.command.CommandSender;
import org.uninstal.seasons.Seasons;
import org.uninstal.seasons.data.SeasonUser;
import org.uninstal.seasons.util.TimeFormat;

import java.util.Optional;

public class SeasonCheckCommand extends SeasonCommand {

    public SeasonCheckCommand(Seasons plugin) {
        super(plugin, 2);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        String userName = args[1];
        Optional<SeasonUser> userOptional = plugin.getServices()
          .getUserService().get(userName);

        if (userOptional.isPresent()) {
            SeasonUser user = userOptional.get();
            sendInfoMessage(sender, user);
        } else {
            plugin.getDatabase().getUser(userName)
              .thenAccept(user -> sendInfoMessage(sender, user));
        }
    }

    private void sendInfoMessage(CommandSender sender, SeasonUser user) {
        if (user == null) {
            sender.sendMessage("§7[§6Сезоны§7] §cПользователь не найден");
            return;
        }

        String message = "§7[§6Сезоны§7] §a%s:\n" +
          "§7[§6Сезоны§7] Ранг: %s\n" +
          "§7[§6Сезоны§7] Прогресс: §e%s§7/§e%s\n" +
          "§7[§6Сезоны§7] Наигранное время: §e%s\n" +
          "§7[§6Сезоны§7] Убитые монстры: §e%s\n" +
          "§7[§6Сезоны§7] Убитые игроки: §e%s\n";

        sender.sendMessage(String.format(message,
          user.getUserName(),
          user.getRank().getDisplay(),
          user.getExp(),
          user.getRank().getExpToNext(),
          TimeFormat.toShort(user.getPlayTimeReal()),
          user.getMobKills(),
          user.getPlayerKills())
        );
    }
}
