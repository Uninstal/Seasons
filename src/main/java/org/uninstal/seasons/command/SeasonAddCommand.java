package org.uninstal.seasons.command;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.uninstal.seasons.Seasons;
import org.uninstal.seasons.data.SeasonUser;
import org.uninstal.seasons.database.UserParameter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class SeasonAddCommand extends SeasonCommand {
    
    private final static List<Material> CHEST_TYPES = Arrays.asList(Material.CHEST, Material.TRAPPED_CHEST);
    
    public SeasonAddCommand(Seasons plugin) {
        super(plugin, 2);
    }
    
    @Override
    public void run(CommandSender sender, String[] args) {
        
        if (args[1].equalsIgnoreCase("chest") && sender instanceof Player) {
            Player player = (Player) sender;
            Block block = player.getTargetBlock(null, 5);
            if (block == null 
              || !CHEST_TYPES.contains(block.getType())) {
                sender.sendMessage("§7[§6Сезоны§7] §cБлок не является сундуком");
                return;
            }
            plugin.getServices()
              .getChestService()
              .add(block);
            sender.sendMessage("§7[§6Сезоны§7] §aБлок был зарегистрирован");
            return;
        }

        String userName = args[2];
        if (args.length >= 4) {
            if (args[1].equalsIgnoreCase("exp")) {
                incrementParameter(sender, userName, Integer.parseInt(args[3]),
                  SeasonUser::addExp, UserParameter.EXP);
            } else if (args[1].equalsIgnoreCase("mobs")) {
                incrementParameter(sender, userName, Integer.parseInt(args[3]),
                  SeasonUser::addMobKills, UserParameter.MOB_KILLS);
            } else if (args[1].equalsIgnoreCase("players")) {
                incrementParameter(sender, userName, Integer.parseInt(args[3]),
                  SeasonUser::addPlayerKills, UserParameter.PLAYER_KILLS);
            }
        }
    }
    
    private <V> void incrementParameter(CommandSender sender, String userName, V value, 
                                        BiConsumer<SeasonUser, V> consumer, UserParameter parameter) {
        Optional<SeasonUser> targetUser = plugin.getServices()
          .getUserService().get(userName);
        
        if (targetUser.isPresent()) {
            consumer.accept(targetUser.get(), value);
            sender.sendMessage("§7[§6Сезоны§7] §aДанные игрока успешно обновлены");
        } else {
            plugin.getDatabase().updateIncrementUser(userName, parameter, value)
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