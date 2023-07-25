package org.uninstal.seasons.command;

import org.bukkit.command.CommandSender;
import org.uninstal.seasons.Seasons;
import org.uninstal.seasons.data.SeasonUser;

public class SeasonRanksCommand extends SeasonCommand {

    public SeasonRanksCommand(Seasons plugin) {
        super(plugin, 1);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        plugin.getDatabase().getRankedPlayers()
          .thenAccept(users -> {
              int position = 1;
              for (SeasonUser user : users) {
                  sender.sendMessage(String.format("§7[§6Сезоны§7] §e%s§7: §6%s EXP §8(%s место)", 
                    user.getUserName(), user.getExp(), position++));
              }
          });
    }
}