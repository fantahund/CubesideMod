package de.fanta.cubeside;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.TextColor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Config {
    public static boolean chattimestamps = false;
    public static TextColor timestampColor = TextColor.parse("#ffffff");

    static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("cubeside.properties");

    static void serialize() {
        Properties prop = new Properties();
        prop.setProperty("enable_chat_time_stamps", Boolean.toString(chattimestamps));
        prop.setProperty("timestamp_color", timestampColor.toString());
        try {
            OutputStream s = Files.newOutputStream(configPath);
            prop.store(s, "Cubeside Config");
            s.close();
        } catch (IOException e) {
            Cubeside.LOGGER.warn("Failed to write config!");
        }
    }

    static void deserialize() {
        Properties prop = new Properties();
        try {
            InputStream s = Files.newInputStream(configPath);
            prop.load(s);
            chattimestamps = Boolean.parseBoolean(prop.getProperty("enable_chat_time_stamps"));
            timestampColor = TextColor.parse(prop.getProperty("timestamp_color"));
        } catch (IOException e) {
            Cubeside.LOGGER.warn("Failed to read config!");
        }
        Config.serialize();
    }
}
