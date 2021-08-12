package de.fanta.cubeside.data;

import de.fanta.cubeside.CubesideClient;
import net.minecraft.text.Text;

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

    private final String addCommandQuery;
    private final String getCommandsQuery;

    private final ExecutorService executor;

    public Database() {
        executor = Executors.newSingleThreadExecutor();
        try {
            Class.forName("org.h2.Driver");
            String dbUrl = "jdbc:h2:chat";
            this.connection = DriverManager.getConnection(dbUrl);

            createTablesIfNotExist();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Could not initialize chat database", e);
        }

        addMessageQuery = "INSERT INTO `messages` VALUES (?, ?, ?)";
        getMessagesQuery = "SELECT `message` FROM `messages` WHERE server = ? ORDER BY `timestamp`";

        addCommandQuery = "INSERT INTO `commands` VALUES (?, ?, ?)";
        getCommandsQuery = "SELECT `command` FROM `commands` WHERE server = ? ORDER BY `timestamp`";
    }

    private void createTablesIfNotExist() throws SQLException {
        PreparedStatement messageStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `messages` (" +
                "`timestamp` BIGINT," +
                "`server` TEXT," +
                "`message` TEXT" +
                ")");

        messageStatement.executeUpdate();

        PreparedStatement commandStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `commands` (" +
                "`timestamp` BIGINT," +
                "`server` TEXT," +
                "`command` TEXT" +
                ")");

        commandStatement.executeUpdate();
    }

    public void addMessage(Text message, String server) {
        long timestamp = System.currentTimeMillis();
        executor.execute(() -> {
            try (PreparedStatement statement = this.connection.prepareStatement(addMessageQuery)) {
                statement.setLong(1, timestamp);
                statement.setString(2, server);
                statement.setString(3, Text.Serializer.toJson(message));

                statement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                CubesideClient.LOGGER.error("Could not add Message to database " + message.getString(), e);
            }
        });
    }

    public List<Text> loadMessages(String server) {
        List<Text> messages = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(getMessagesQuery)) {
            statement.setString(1, server);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Text message = Text.Serializer.fromJson(rs.getString(1));
                messages.add(message);
            }
        } catch (SQLException e) {
            CubesideClient.LOGGER.error("Could not load messages for server " + server, e);
        }
        return messages;
    }

    public void addCommand(String command, String server) {
        long timestamp = System.currentTimeMillis();
        executor.execute(() -> {
            try (PreparedStatement statement = this.connection.prepareStatement(addCommandQuery)) {
                statement.setLong(1, timestamp);
                statement.setString(2, server);
                statement.setString(3, command);

                statement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                // TODO log
                CubesideClient.LOGGER.error("Could not add Command to database " + command, e);
            }
        });
    }

    public List<String> loadCommands(String server) {
        List<String> commands = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(getCommandsQuery)) {
            statement.setString(1, server);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String command = rs.getString(1);
                commands.add(command);
            }
        } catch (SQLException e) {
            // TODO log
            CubesideClient.LOGGER.error("Could not load messages for server " + server, e);
        }
        return commands;
    }

}
