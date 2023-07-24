package org.uninstal.seasons.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.uninstal.discordbot.DiscordBot;
import org.uninstal.discordbot.DiscordChannel;

public class Message {

    private final String message;

    public Message(String message) {
        this.message = message.replace("&", "§");
    }
    
    public void sendMessage(Player player, MessageReplacer replacer) {
        player.sendMessage(message);
    }
    
    public void sendNotification(DiscordChannel channel, MessageReplacer replacer) {
        DiscordBot.getService().sendMessage(channel, replacer.format(message).replaceAll("&.|§.", ""));
    }
    
    public void sendNotificationAll(MessageReplacer replacer) {
        Bukkit.getOnlinePlayers().forEach(player -> sendMessage(player, replacer));
        sendNotification(DiscordChannel.RADIO, replacer);
    }
}