package org.uninstal.seasons.database;

import org.apache.commons.lang.Validate;
import org.uninstal.seasons.Seasons;
import org.uninstal.seasons.data.SeasonUser;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class SeasonDatabase {

    private final ExecutorService executor = Executors.newScheduledThreadPool(3);
    private final BestPlayersHolder holder;
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
        this.holder = new BestPlayersHolder(plugin);
        this.host = host;
        this.base = base;
        this.user = user;
        this.password = password;
    }

    private Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://<host>/<base>?useSSL=false".replace("<host>", host).replace("<base>", base), user, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <V> V execute(String command, SQLFunction<V> function) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(command)) {
            return function.apply(statement);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void execute(Consumer<Connection> consumer) {
        try (Connection connection = getConnection()) {
            consumer.accept(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <V> CompletableFuture<V> executeAsync(String command, SQLFunction<V> function) {
        return CompletableFuture.supplyAsync(() -> {
            synchronized (SeasonDatabase.this) {
                return execute(command, function);
            }
        }, executor);
    }

    public void init() {
        execute(connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS apocalypseseasons ("
                  + "user_name VARCHAR(20) NOT NULL PRIMARY KEY, "
                  + "exp INT NOT NULL, "
                  + "mob_kills INT NOT NULL, "
                  + "player_kills INT NOT NULL, "
                  + "play_time BIGINT NOT NULL)");
                statement.executeUpdate("ALTER TABLE apocalypseseasons CONVERT TO CHARACTER SET utf8 COLLATE utf8_bin");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<SeasonUser> getUser(String userName) {
        Validate.notNull(userName, "Username cannot be null");
        return executeAsync("SELECT exp, mob_kills, player_kills, play_time FROM apocalypseseasons WHERE user_name=?",
          statement -> {
              statement.setString(1, userName);
              ResultSet resultSet = statement.executeQuery();
              return !resultSet.next() ? null : wrapUser(userName,
                resultSet.getInt(1),
                resultSet.getInt(2),
                resultSet.getInt(3),
                resultSet.getLong(4));
          });
    }

    public void saveUser(SeasonUser user, boolean async) {
        Validate.notNull(user, "User cannot be null");
        String command = "INSERT INTO apocalypseseasons (user_name, exp, mob_kills, player_kills, play_time) " +
          "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE exp=?, mob_kills=?, player_kills=?, play_time=?";
        SQLFunction<SeasonUser> function = statement -> {
            statement.setString(1, user.getUserName());
            statement.setInt(2, user.getExp());
            statement.setInt(3, user.getMobKills());
            statement.setInt(4, user.getPlayerKills());
            statement.setLong(5, user.getPlayTime());
            statement.setInt(6, user.getExp());
            statement.setInt(7, user.getMobKills());
            statement.setInt(8, user.getPlayerKills());
            statement.setLong(9, user.getPlayTime());
            statement.executeUpdate();
            return user;
        };

        if (async) executeAsync(command, function);
        else execute(command, function);
    }

    public CompletableFuture<BestPlayersList> getBestPlayers(UserParameter parameter, int quantity) {
        Validate.notNull(parameter, "Target parameter cannot be null");
        Validate.isTrue(quantity > 0, "Quantity cannot be less than 0");
        return executeAsync("SELECT * FROM apocalypseseasons ORDER BY " + parameter.getColumnId() + " DESC LIMIT ?",
          statement -> {
              statement.setInt(1, quantity);
              ResultSet resultSet = statement.executeQuery();
              List<SeasonUser> users = new ArrayList<>();
              while (resultSet.next()) {
                  users.add(
                    wrapUser(resultSet.getString(1),
                      resultSet.getInt(2), resultSet.getInt(3),
                      resultSet.getInt(4), resultSet.getLong(5))
                  );
              }
              return new BestPlayersList(parameter, users);
          });
    }

    public CompletableFuture<List<SeasonUser>> getRankedPlayers() {
        return executeAsync("SELECT * FROM apocalypseseasons WHERE exp>0 ORDER BY exp DESC",
          statement -> {
              List<SeasonUser> users = new ArrayList<>();
              ResultSet result = statement.executeQuery();
              while (result.next()) {
                  users.add(
                    wrapUser(result.getString(1),
                      result.getInt(2), result.getInt(3),
                      result.getInt(4), result.getLong(5)
                    )
                  );
              }
              return users;
          });
    }

    public void clear() {
        execute(connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("DELETE FROM apocalypseseasons WHERE exp=0");
                statement.executeUpdate("UPDATE apocalypseseasons SET mob_kills=0, player_kills=0, play_time=0");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public BestPlayersHolder getBestPlayersHolder() {
        return holder;
    }

    public <V> CompletableFuture<Integer> updateUser(String userName, UserParameter parameter, V value) {
        Validate.notNull(userName, "Username cannot be null");
        Validate.notNull(parameter, "Parameter cannot be null");
        Validate.notNull(value, "Value cannot be null");
        return executeAsync("UPDATE apocalypseseasons SET " + parameter.getColumnId() + "=? WHERE user_name=?",
          statement -> {
              statement.setObject(1, value);
              statement.setString(2, userName);
              return statement.executeUpdate();
          });
    }

    public <V> CompletableFuture<Integer> updateIncrementUser(String userName, UserParameter parameter, V value) {
        Validate.notNull(userName, "Username cannot be null");
        Validate.notNull(parameter, "Parameter cannot be null");
        Validate.notNull(value, "Value cannot be null");
        return executeAsync("UPDATE apocalypseseasons SET " + parameter.getColumnId()
            + "=" + parameter.getColumnId() + "+? WHERE user_name=?",
          statement -> {
              statement.setObject(1, value);
              statement.setString(2, userName);
              return statement.executeUpdate();
          });
    }

    private SeasonUser wrapUser(String userName, int exp, int mobKills, int playerKills, long playTime) {
        return new SeasonUser(plugin.getServices(), userName, exp, mobKills, playerKills, playTime);
    }

    public interface SQLFunction<V> {
        V apply(PreparedStatement preparedStatement) throws SQLException;
    }
}