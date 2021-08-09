package de.fanta.cubeside.data;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private final Connection connection;

    private final String addMessageQuery;
    private final String getMessagesQuery;

    public Database() {
        try {
            Class.forName("org.sqlite.JDBC");
            String dbUrl = "jdbc:sqlite:chat.db";
            this.connection = DriverManager.getConnection(dbUrl);

            createTablesIfNotExist();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Could not initialize chat database", e);
        }

        addMessageQuery = "INSERT INTO `messages` VALUES (?, ?, ?)";
        getMessagesQuery = "SELECT `message` FROM `messages` WHERE server = ? ORDER BY `timestamp` DESC";
    }

    private void createTablesIfNotExist() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `messages` (" +
                "`timestamp` INTEGER," +
                "`server` TEXT," +
                "`message` TEXT" +
                ")");

        statement.executeUpdate();
    }

    public void addMessage(Text message, String server) {
        long timestamp = System.currentTimeMillis();
        new Thread(() -> {
            try {
                PreparedStatement statement = this.connection.prepareStatement(addMessageQuery);

                statement.setLong(1, timestamp);
                statement.setString(2, server);
                statement.setString(3, message.getString());

                statement.executeUpdate();
            } catch (SQLException e) {
                // TODO log
                System.out.println("Could not add message " + message.getString());
            }
        }).start();
    }

    public List<Text> loadMessages(String server) {
        List<Text> messages = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(getMessagesQuery);
            statement.setString(1, server);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Text message = new LiteralText(rs.getString(1));
                messages.add(message);
            }
        } catch (SQLException e) {
            // TODO log
            System.out.println("Could not load messages for server " + server);
        }
        return messages;
    }
}
