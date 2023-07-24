package org.uninstal.seasons.command;

import org.bukkit.command.CommandSender;
import org.uninstal.seasons.Seasons;
import org.uninstal.seasons.data.SeasonUser;
import org.uninstal.seasons.database.UserParameter;

import java.util.Optional;
import java.util.function.BiConsumer;

public class SeasonSetCommand extends SeasonCommand {

    public SeasonSetCommand(Seasons plugin) {
        super(plugin, 3);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        String userName = args[2];

        if (args[1].equalsIgnoreCase("exp")) {
            setParameter(sender, userName, Integer.parseInt(args[3]),
              SeasonUser::setExp, UserParameter.EXP);
        } else if (args[1].equalsIgnoreCase("mobs")) {
            setParameter(sender, userName, Integer.parseInt(args[3]),
              SeasonUser::setMobKills, UserParameter.MOB_KILLS);
        } else if (args[1].equalsIgnoreCase("players")) {
            setParameter(sender, userName, Integer.parseInt(args[3]),
              SeasonUser::setPlayerKills, UserParameter.PLAYER_KILLS);
        }
    }

    private <V> void setParameter(CommandSender sender, String userName, V value,
                                  BiConsumer<SeasonUser, V> consumer, UserParameter parameter) {
        Optional<SeasonUser> targetUser = plugin.getServices()
          .getUserService().get(userName);

        if (targetUser.isPresent()) {
            consumer.accept(targetUser.get(), value);
            sender.sendMessage("§7[§6Сезоны§7] §aДанные игрока успешно обновлены");
        } else {
            plugin.getDatabase().updateUser(userName, parameter, value)
              .thenAccept(integer -> {
                  if (integer == 0) {
                      sender.sendMessage("§7[§6Сезоны§7] §cПользователь не найден");
                  } else {
                      sender.sendMessage("§7[§6Сезоны§7] §aДанные базы данных успешно обновлены");
                  }
              });
        }
    }
}