package org.uninstal.seasons.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.uninstal.seasons.Seasons;

public class SeasonCleanCommand extends SeasonCommand {
    
    private int count = 0;
    
    public SeasonCleanCommand(Seasons plugin) {
        super(plugin, 1);
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            if (count < 3) {
                sender.sendMessage("§7[§6Сезоны§7] До очистки: " + (3 - count++));
            } else {
                plugin.getLogger().info("Season was ended, running cleaner...");
                plugin.getServices().getCleaner().clean();
            }
        }
    }
}
