package org.uninstal.seasons;

import org.uninstal.seasons.data.SeasonUser;

import java.sql.*;
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
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS apocalypseseasons");
//                  "id INT NOT NULL AUTO_INCREMENT, " +
//                  "owner VARCHAR(30) NOT NULL, " +
//                  "item BLOB NOT NULL, " +
//                  "price DOUBLE NOT NULL, " +
//                  "state TINYINT UNSIGNED NOT NULL, " +
//                  "date BIGINT UNSIGNED NOT NULL, " +
//                  "PRIMARY KEY (id))");
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
    
    private SeasonUser wrapUser(String userName, int exp, int mobKills, int playerKills, int playTime) {
        return new SeasonUser(plugin.getServices(), userName, exp, mobKills, playerKills, playTime);
    }
}