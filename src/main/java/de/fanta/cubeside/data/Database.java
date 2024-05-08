package de.fanta.cubeside.data;

import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import de.fanta.cubeside.CubesideClientFabric;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.world.PersistentStateManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Database {
    private final Connection connection;

    private final String addMessageQuery;
    private final String getMessagesQuery;
    private final String deleteOldMessagesQuery;

    private final String addCommandQuery;
    private final String getCommandsQuery;
    private final String deleteOldCommandsQuery;

    private final ExecutorService executor;
    private final String deleteNewestMessageQuery;

    public Database() {
        executor = Executors.newSingleThreadExecutor();
        try {
            Class.forName("org.h2.Driver");
            String dbUrl = "jdbc:h2:" + CubesideClientFabric.getConfigDirectory() + "/chatNew";
            this.connection = DriverManager.getConnection(dbUrl);

            createTablesIfNotExist();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Could not initialize chat database", e);
        }

        //addMessageQuery = "INSERT INTO `messages` VALUES (?, ?, ?)";
        addMessageQuery = "INSERT INTO messages(message, server, timestamp) VALUES (?, ?, ?)";
        getMessagesQuery = "SELECT `message` FROM `messages` WHERE server = ? ORDER BY `id`";
        deleteOldMessagesQuery = "DELETE FROM `messages` WHERE `timestamp` <= ?";
        //deleteNewestMessageQuery = "DELETE FROM `messages` ORDER BY id DESC LIMIT 1";
        deleteNewestMessageQuery = "DELETE FROM `messages` WHERE id = (SELECT MAX(id) FROM `messages`)";

        //addCommandQuery = "INSERT INTO `commands` VALUES (?, ?, ?)";
        addCommandQuery = "INSERT INTO commands(command, server, timestamp) VALUES (?, ?, ?)";
        getCommandsQuery = "SELECT `command` FROM `commands` WHERE server = ? ORDER BY `id`";
        deleteOldCommandsQuery = "DELETE FROM `commands` WHERE `timestamp` <= ?";
    }

    private void createTablesIfNotExist() throws SQLException {
        if (CubesideClientFabric.databaseinuse) {
            return;
        }
        PreparedStatement messageStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `messages` (" +
                "`id` INT NOT NULL AUTO_INCREMENT," +
                "`message` TEXT," +
                "`server` TEXT," +
                "`timestamp` BIGINT," +
                "PRIMARY KEY (id)" +
                ")");

        messageStatement.executeUpdate();

        PreparedStatement commandStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `commands` (" +
                "`id` INT NOT NULL AUTO_INCREMENT," +
                "`command` TEXT," +
                "`server` TEXT," +
                "`timestamp` BIGINT," +
                "PRIMARY KEY (id)" +
                ")");
        commandStatement.executeUpdate();
    }

    public void addMessage(Text message, String server) {
        if (CubesideClientFabric.databaseinuse) {
            return;
        }
        long timestamp = System.currentTimeMillis();
        executor.execute(() -> {
            try (PreparedStatement statement = this.connection.prepareStatement(addMessageQuery)) {
                //statement.setString(1, Text.Serialization.toJsonString(message));
                statement.setString(1, Text.Serialization.toJsonString(message, DynamicRegistryManager.EMPTY));
                statement.setString(2, server);
                statement.setLong(3, timestamp);

                statement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                CubesideClientFabric.LOGGER.error("Could not add Message to database " + message.getString(), e);
            }
        });
    }

    public List<Text> loadMessages(String server) {
        if (CubesideClientFabric.databaseinuse) {
            return null;
        }
        List<Text> messages = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(getMessagesQuery)) {
            statement.setString(1, server);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                try {
                    Text message = Text.Serialization.fromJson(rs.getString(1), DynamicRegistryManager.EMPTY);
                    messages.add(message);
                } catch (JsonParseException ignore) {
                }
            }
        } catch (SQLException e) {
            CubesideClientFabric.LOGGER.error("Could not load messages for server " + server, e);
        }
        return messages;
    }

    public void addCommand(String command, String server) {
        if (CubesideClientFabric.databaseinuse) {
            return;
        }
        long timestamp = System.currentTimeMillis();
        executor.execute(() -> {
            try (PreparedStatement statement = this.connection.prepareStatement(addCommandQuery)) {
                statement.setString(1, command);
                statement.setString(2, server);
                statement.setLong(3, timestamp);

                statement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                CubesideClientFabric.LOGGER.error("Could not add Command to database " + command, e);
            }
        });
    }

    public List<String> loadCommands(String server) {
        if (CubesideClientFabric.databaseinuse) {
            return null;
        }
        List<String> commands = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(getCommandsQuery)) {
            statement.setString(1, server);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String command = rs.getString(1);
                commands.add(command);
            }
        } catch (SQLException e) {
            CubesideClientFabric.LOGGER.error("Could not load messages for server " + server, e);
        }
        return commands;
    }

    public void deleteOldMessages(long days) throws SQLException {
        if (CubesideClientFabric.databaseinuse) {
            return;
        }
        executor.execute(() -> {
            try (PreparedStatement statement = this.connection.prepareStatement(deleteOldMessagesQuery)) {
                statement.setLong(1, System.currentTimeMillis() - days * 24 * 60 * 60 * 1000);
                CubesideClientFabric.LOGGER.info("Delete Messages of the last " + days + " days");
                CubesideClientFabric.LOGGER.info(statement.executeUpdate() + " messages were deleted");
                connection.commit();
            } catch (SQLException e) {
                CubesideClientFabric.LOGGER.error("Could not delete old Messages from database", e);
            }
        });
    }

    public void deleteOldCommands(long days) throws SQLException {
        if (CubesideClientFabric.databaseinuse) {
            return;
        }
        executor.execute(() -> {
            try (PreparedStatement statement = this.connection.prepareStatement(deleteOldCommandsQuery)) {
                statement.setLong(1, System.currentTimeMillis() - days * 24 * 60 * 60 * 1000);
                CubesideClientFabric.LOGGER.info("Delete command of the last " + days + " days");
                CubesideClientFabric.LOGGER.info(statement.executeUpdate() + " commands were deleted");
                connection.commit();
            } catch (SQLException e) {
                CubesideClientFabric.LOGGER.error("Could not delete old Commands from database", e);
            }
        });
    }

    public void deleteNewestMessage() {
        if (CubesideClientFabric.databaseinuse) {
            return;
        }
        executor.execute(() -> {
            try (PreparedStatement statement = this.connection.prepareStatement(deleteNewestMessageQuery)) {
                statement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                CubesideClientFabric.LOGGER.error("Could not delete Oldest Message from database", e);
            }
        });
    }
}
