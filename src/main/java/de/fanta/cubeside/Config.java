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
    public static boolean saveMessagestoDatabase = false;
    public static boolean showInvisibleArmorstands = true;
    public static boolean showInvisibleEntitiesinSpectator = true;
    public static boolean simpleSignGlow = false;
    public static boolean afkPling = false;
    public static TextColor timestampColor = TextColor.parse("#ffffff");
    public static int chatMessageLimit = 100;

    //gamma
    public static double minGamma = 1.0;
    public static double maxGamma = 12.0;


    //Eiki
    public static boolean autochat = false;
    public static String antwort = "Ich habe grade leider keine Zeit!";

    //Viewdistance
    public static boolean fullverticalview = true;
    public static boolean unloadchunks = true;
    public static int fakeviewdistance = 32;

    //tpa
    public static boolean clickabletpamessage = true;
    public static boolean tpasound = true;

    //christmas
    public static boolean removeChristmasChest = false;


    static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("cubeside.properties");

    static void serialize() {
        Properties prop = new Properties();
        prop.setProperty("enable_chat_time_stamps", Boolean.toString(chattimestamps));
        prop.setProperty("drop_item_fancy", Boolean.toString(dropItemFancy));
        prop.setProperty("timestamp_color", timestampColor.toString());
        prop.setProperty("chat_message_limit", String.valueOf(chatMessageLimit));
        prop.setProperty("third_person_elytra", String.valueOf(thirdPersonElytra));
        prop.setProperty("elytra_alarm", String.valueOf(elytraAlarm));
        prop.setProperty("save_messages_to_database", String.valueOf(saveMessagestoDatabase));
        prop.setProperty("show_invisible_armorstands", String.valueOf(showInvisibleArmorstands));
        prop.setProperty("show_invisible_entities_in_Spectator", String.valueOf(showInvisibleEntitiesinSpectator));
        prop.setProperty("simple_sign_glow", String.valueOf(simpleSignGlow));
        prop.setProperty("afk_pling", String.valueOf(afkPling));

        //Gamma
        prop.setProperty("minGamma", String.valueOf(Double.valueOf(minGamma)));
        prop.setProperty("maxGamma", String.valueOf(Double.valueOf(maxGamma)));

        //Eiki
        prop.setProperty("autochat", Boolean.toString(autochat));
        prop.setProperty("antwort", antwort);

        //Viewdistance
        prop.setProperty("full_vertical_view", String.valueOf(fullverticalview));
        prop.setProperty("unload_chunks", String.valueOf(unloadchunks));
        prop.setProperty("fake_view_distance", String.valueOf(fakeviewdistance));

        //tpa
        prop.setProperty("clickable_tpa_message", String.valueOf(clickabletpamessage));
        prop.setProperty("tpa_sound", String.valueOf(tpasound));

        //christmas
        prop.setProperty("remove_christmas", String.valueOf(removeChristmasChest));

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
            saveMessagestoDatabase = Boolean.parseBoolean(prop.getProperty("save_messages_to_database", "false"));
            showInvisibleArmorstands = Boolean.parseBoolean(prop.getProperty("show_invisible_armorstands", "true"));
            showInvisibleEntitiesinSpectator = Boolean.parseBoolean(prop.getProperty("show_invisible_entities_in_Spectator", "true"));
            simpleSignGlow = Boolean.parseBoolean(prop.getProperty("simple_sign_glow", "false"));
            afkPling = Boolean.parseBoolean(prop.getProperty("afk_pling", "false"));

            //Gamma
            minGamma = Double.parseDouble(prop.getProperty("minGamma", "1.0"));
            maxGamma = Double.parseDouble(prop.getProperty("maxGamma", "12.0"));

            //Eiki
            autochat = Boolean.parseBoolean(prop.getProperty("autochat", "false"));
            antwort = prop.getProperty("antwort", antwort);

            //Viewdistance
            fullverticalview = Boolean.parseBoolean(prop.getProperty("full_vertical_view", "true"));
            unloadchunks = Boolean.parseBoolean(prop.getProperty("unload_chunks", "true"));
            fakeviewdistance = Integer.parseInt(prop.getProperty("fake_view_distance", String.valueOf(fakeviewdistance)));

            //tpa
            clickabletpamessage = Boolean.parseBoolean(prop.getProperty("clickable_tpa_message", "true"));
            tpasound = Boolean.parseBoolean(prop.getProperty("tpa_sound", "true"));

            //christmas
            removeChristmasChest = Boolean.parseBoolean(prop.getProperty("remove_christmas", "false"));

        } catch (IOException e) {
            CubesideClient.LOGGER.warn("Failed to read config!");
        }
        Config.serialize();
    }
}
