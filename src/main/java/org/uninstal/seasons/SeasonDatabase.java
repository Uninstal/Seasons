package org.uninstal.seasons;

import org.apache.commons.lang.Validate;
import org.uninstal.seasons.data.SeasonUser;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

public class SeasonDatabase {

    private final ExecutorService executor = Executors.newScheduledThreadPool(3);
    private final Seasons plugin;
    private final String host;
    private final String base;
    private final String user;
    private final String password;

    public SeasonDatabase(Seasons plugin, String host, String base, String user, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver").getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.plugin = plugin;
        this.host = host;
        this.base = base;
        this.user = user;
        this.password = password;
    }

    private Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://<host>/<base>?useSSL=false"
                .replace("<host>", host).replace("<base>", base),
              user, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <V> V execute(Function<Connection, V> function) {
        try (Connection connection = getConnection()) {
            return function.apply(connection);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void execute(Consumer<Connection> consumer) {
        try (Connection connection = getConnection()) {
            consumer.accept(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private <V> CompletableFuture<V> executeAsync(Function<Connection, V> function) {
        return CompletableFuture.supplyAsync(() -> {
            synchronized (SeasonDatabase.this) {
                return execute(function);
            }
        }, executor);
    }

    protected void init() {
        execute(connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(
                  "CREATE TABLE IF NOT EXISTS apocalypseseasons (" +
                    "user_name TEXT NOT NULL, " +
                    "exp INT NOT NULL, " +
                    "mob_kills INT NOT NULL, " +
                    "player_kills INT NOT NULL, " +
                    "play_time LONG NOT NULL)"
                );
                statement.executeUpdate("ALTER TABLE apocalypseseasons CONVERT TO CHARACTER SET utf8 COLLATE utf8_bin");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<SeasonUser> getUser(String userName) {
        return executeAsync(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("")) {
                statement.setString(1, userName);
                ResultSet resultSet = statement.executeQuery();
                return wrapUser(userName,
                  resultSet.getInt(1),
                  resultSet.getInt(2),
                  resultSet.getInt(3),
                  resultSet.getInt(4));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<SeasonUser> saveUser(SeasonUser user) {
        return executeAsync(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("")) {
                statement.setString(1, user.getUserName());
                statement.setInt(2, user.getExp());
                statement.setInt(3, user.getPlayerKills());
                statement.setInt(4, user.getMobKills());
                statement.setInt(5, user.getPlayTime());
                statement.executeUpdate();
                return user;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<BestPlayersList> getBestPlayers(TargetParameter parameter, int quantity) {
        Validate.notNull(parameter, "Target parameter cannot be null");
        Validate.isTrue(quantity > 0, "Quantity cannot be less than 0");
        return executeAsync(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("")) {
                statement.setString(1, parameter.getColumnId());
                ResultSet resultSet = statement.executeQuery();
                List<SeasonUser> users = new ArrayList<>();
                while (resultSet.next()) {
                    users.add(
                      wrapUser(
                        resultSet.getString(1),
                        resultSet.getInt(2),
                        resultSet.getInt(3),
                        resultSet.getInt(4),
                        resultSet.getInt(5)
                      )
                    );
                }
                return new BestPlayersList(parameter, users);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private SeasonUser wrapUser(String userName, int exp, int mobKills, int playerKills, int playTime) {
        return new SeasonUser(plugin.getServices(), userName, exp, mobKills, playerKills, playTime);
    }

    public enum TargetParameter {
        PLAY_TIME("play_time", "play-time"),
        MOB_KILLS("mob_kills", "mob-kills"),
        PLAYER_KILLS("player_kills", "player-kills");

        private final String columnId;
        private final String configId;

        TargetParameter(String columnId, String configId) {
            this.columnId = columnId;
            this.configId = configId;
        }

        public String getColumnId() {
            return columnId;
        }

        public String getConfigId() {
            return configId;
        }
    }
    
    public static class BestPlayersList {
        
        private final TargetParameter parameter;
        private final List<SeasonUser> users;

        public BestPlayersList(TargetParameter parameter, List<SeasonUser> users) {
            this.parameter = parameter;
            this.users = users;
        }

        public TargetParameter getParameter() {
            return parameter;
        }

        public List<SeasonUser> getUsers() {
            return users;
        }
    }
}