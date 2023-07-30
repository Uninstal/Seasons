package org.uninstal.seasons.integration;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.uninstal.seasons.Seasons;
import org.uninstal.seasons.data.SeasonRank;
import org.uninstal.seasons.data.SeasonUser;
import org.uninstal.seasons.database.UserParameter;
import org.uninstal.seasons.util.TimeFormat;

import java.util.function.Function;

public class Placeholder extends PlaceholderExpansion {

    private final Seasons plugin;

    public Placeholder(Seasons plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "season";
    }

    @Override
    public String getAuthor() {
        return "Uninstal";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {
        return onPlaceholderRequest((Player) p, params);
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {

        if (params.startsWith("user_")) {
            String userName = p.getName();

            if (params.endsWith("rank")) {
                return processUser(userName, user -> (user.getRank().getDisplay() + " ").trim(), "");
            }
            if (params.endsWith("exp")) {
                return processUser(userName, SeasonUser::getExp, "0");
            }
            if (params.endsWith("time")) {
                return processUser(userName, user -> TimeFormat.toShort(user.getPlayTime()), "§e0с");
            }
            if (params.endsWith("mobs")) {
                return processUser(userName, SeasonUser::getMobKills, "0");
            }
            if (params.endsWith("players")) {
                return processUser(userName, SeasonUser::getPlayerKills, "0");
            }
            if (params.endsWith("rank_next")) {
                return processUser(userName, user -> plugin.getServices().getRankService()
                    .getByExp(user.getRank().getExpToNext())
                    .map(SeasonRank::getDisplay).orElse(""),
                  "");
            }
            if (params.endsWith("exp_next")) {
                return processUser(userName, user -> user.getRank().getExpToNext(), "100");
            }
        }
        if (params.equalsIgnoreCase("date_hours")) {
            return String.valueOf((System.currentTimeMillis() - plugin.getCurrentSeason().getStart().getTimeInMillis()) / 3600000L);
        }
        if (params.equalsIgnoreCase("date_remain")) {
            long end = plugin.getCurrentSeason().getEnd().getTimeInMillis();
            return TimeFormat.toFull(end - System.currentTimeMillis());
        }
        if (params.startsWith("rating_")) {
            return processRatingPlaceholder(p, params.substring(7));
        }
        return "";
    }

    private String processUser(String userName, Function<SeasonUser, Object> function, String defaultValue) {
        return plugin.getServices().getUserService()
          .get(userName)
          .map(seasonUser -> function.apply(seasonUser).toString())
          .orElse(defaultValue);
    }

    private String processRatingPlaceholder(Player player, String params) {
        UserParameter parameter = UserParameter
          .getByPlaceholderId(params.substring(0, params.indexOf("_")));
        if (parameter == null) {
            return "nullParam";
        }

        params = params.substring(parameter.getPlaceholderId().length() + 1);
        int place = Integer.parseInt(params.substring(0, params.indexOf("_")));
        params = params.substring(params.indexOf("_") + 1);

        SeasonUser targetUser = plugin.getDatabase()
          .getBestPlayersHolder()
          .get(parameter, place);
        if (targetUser == null) {
            if (params.equalsIgnoreCase("name")) {
                return "§8Пусто";
            } else if (params.equalsIgnoreCase("value")) {
                return parameter == UserParameter.PLAY_TIME ? "§80с" : "§80";
            } else return "nullParam2";
        }

        if (params.equalsIgnoreCase("name")) {
            return targetUser.getUserName();
        } else if (params.equalsIgnoreCase("value")) {
            return String.valueOf(targetUser.getValue(parameter));
        }

        return "unkParam";
    }
}