package org.uninstal.seasons.command;

import org.bukkit.command.CommandSender;
import org.uninstal.seasons.Seasons;

public class SeasonUpdateCommand extends SeasonCommand {
    
    public SeasonUpdateCommand(Seasons plugin) {
        super(plugin, 1);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        plugin.getDatabase().getBestPlayersHolder().update();
        sender.sendMessage("§7[§6Сезоны§7] §aДанные рейтинга игроков успешно обновлены");
    }
}
