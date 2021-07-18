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
    public static boolean dropItemFancy = false;
    public static boolean thirdPersonElytra = false;
    public static boolean elytraAlarm = false;
    public static boolean clearchatbydisconnect = true;
    public static boolean showInvisibleArmorstands = true;
    public static boolean showInvisibleEntitiesinSpectator = true;
    public static boolean simpleSignGlow = true;
    public static TextColor timestampColor = TextColor.parse("#ffffff");
    public static int chatMessageLimit = 100;

    //Eiki
    public static boolean autochat = false;
    public static String antwort = "Ich habe grade leider keine Zeit!";

    static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("cubeside.properties");

    static void serialize() {
        Properties prop = new Properties();
        prop.setProperty("enable_chat_time_stamps", Boolean.toString(chattimestamps));
        prop.setProperty("drop_item_fancy", Boolean.toString(dropItemFancy));
        prop.setProperty("timestamp_color", timestampColor.toString());
        prop.setProperty("chat_message_limit", String.valueOf(chatMessageLimit));
        prop.setProperty("third_person_elytra", String.valueOf(thirdPersonElytra));
        prop.setProperty("elytra_alarm", String.valueOf(elytraAlarm));
        prop.setProperty("clear_chat_by_disconnect", String.valueOf(clearchatbydisconnect));
        prop.setProperty("show_invisible_armorstands", String.valueOf(showInvisibleArmorstands));
        prop.setProperty("show_invisible_entities_in_Spectator", String.valueOf(showInvisibleEntitiesinSpectator));
        prop.setProperty("simple_sign_glow", String.valueOf(simpleSignGlow));

        //Eiki
        prop.setProperty("autochat", Boolean.toString(autochat));
        prop.setProperty("antwort", antwort);
        try {
            OutputStream s = Files.newOutputStream(configPath);
            prop.store(s, "Cubeside Config");
            s.close();
        } catch (IOException e) {
            CubesideClient.LOGGER.warn("Failed to write config!");
        }
    }

    static void deserialize() {
        Properties prop = new Properties();
        try {
            InputStream s = Files.newInputStream(configPath);
            prop.load(s);
            chattimestamps = Boolean.parseBoolean(prop.getProperty("enable_chat_time_stamps", "false"));
            timestampColor = TextColor.parse(prop.getProperty("timestamp_color", "#ffffff"));
            chatMessageLimit = Integer.parseInt(prop.getProperty("chat_message_limit", "100"));
            dropItemFancy = Boolean.parseBoolean(prop.getProperty("drop_item_fancy", "false"));
            thirdPersonElytra = Boolean.parseBoolean(prop.getProperty("third_person_elytra", "false"));
            elytraAlarm = Boolean.parseBoolean(prop.getProperty("elytra_alarm", "false"));
            clearchatbydisconnect = Boolean.parseBoolean(prop.getProperty("clear_chat_by_disconnect", "true"));
            showInvisibleArmorstands = Boolean.parseBoolean(prop.getProperty("show_invisible_armorstands", "true"));
            showInvisibleEntitiesinSpectator = Boolean.parseBoolean(prop.getProperty("show_invisible_entities_in_Spectator", "true"));
            simpleSignGlow = Boolean.parseBoolean(prop.getProperty("simple_sign_glow", "false"));

            //Eiki
            autochat = Boolean.parseBoolean(prop.getProperty("autochat", "false"));
            antwort = prop.getProperty("antwort", antwort);
        } catch (IOException e) {
            CubesideClient.LOGGER.warn("Failed to read config!");
        }
        Config.serialize();
    }
}
