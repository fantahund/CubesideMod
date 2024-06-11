package de.fanta.cubeside;

import de.fanta.cubeside.config.Configs;
import de.fanta.cubeside.data.ChatDatabase;
import de.fanta.cubeside.data.Database;
import de.fanta.cubeside.event.CubesideModChannelHandler;
import de.fanta.cubeside.util.ChatInfo;
import de.iani.cubesideutils.fabric.scheduler.Scheduler;
import fi.dy.masa.malilib.util.FileUtils;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.mvstore.MVStoreModule;

@Environment(EnvType.CLIENT)
public class CubesideClientFabric implements ClientModInitializer {

    public static final String MODID = "cubeside";
    public static final String PREFIX = "§9[§aCubeside§9] ";
    public static final Logger LOGGER = LogManager.getLogger("CubesideMod");
    private static Database DATABASE;
    // GAMA
    public static double minGamma = -1.5;
    public static double maxGamma = 12.0;

    public static double brightnessSliderInterval = 0.05;

    public static Commands commands;

    private static boolean loadingMessages;
    public static boolean databaseinuse = false;
    public static List<Text> messageQueue = new ArrayList<>();

    private static boolean xaeroFairPlay;

    private static long time;

    private static ChatInfo chatInfo;

    private static File configDirectory;
    private static ChatDatabase chatDatabase;

    @Override
    public void onInitializeClient() {
        configDirectory = new File(FileUtils.getConfigDirectory().getPath() + "/CubesideMod");
        if (!configDirectory.isDirectory()) {
            configDirectory.mkdirs();
        }
        File chatStorage = new File(configDirectory, "/chatStorage");
        if (!chatStorage.isDirectory()) {
            chatStorage.mkdir();
        }
        try {
            DATABASE = new Database();
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Du hast scheinbar mehrere Minecraft Instanzen am laufen. Chat & Commands werden nicht gespeichert oder geladen!", e);
            databaseinuse = true;
        }
        Configs.loadFromFile();

        KeyBinds keyBinds = new KeyBinds();
        keyBinds.initKeys();

        Events events = new Events();
        events.init();

        LogicalZoom logicalZoom = new LogicalZoom();
        logicalZoom.initLogicalZoom();
        new CubesideModChannelHandler();

        LOGGER.info(MODID + "Mod Loaded");
        commands = new Commands();

        if (!databaseinuse) {
            try {
                getDatabase().deleteOldMessages(Configs.Chat.DaysTheMessagesAreStored.getIntegerValue());
                getDatabase().deleteOldCommands(Configs.Chat.DaysTheMessagesAreStored.getIntegerValue());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        xaeroFairPlay = FabricLoader.getInstance().isModLoaded("xaerominimapfair");

        time = 0;
        this.restartTask(1);
    }

    public static Database getDatabase() {
        return DATABASE;
    }

    public static void setLoadingMessages(boolean setloadingMessages) {
        loadingMessages = setloadingMessages;
    }

    public static boolean isLoadingMessages() {
        return loadingMessages;
    }

    public static boolean isXaeroFairPlay() {
        return xaeroFairPlay;
    }

    public void restartTask(int l) {
        Scheduler.scheduleSyncRepeatingTask(() -> time++, 0, l);
    }

    public static long getTime() {
        return time;
    }

    public static ChatInfo getChatInfo() {
        return chatInfo;
    }

    public static void setChatInfo(ChatInfo chatInfo) {
        CubesideClientFabric.chatInfo = chatInfo;
    }

    public static File getConfigDirectory() {
        return configDirectory;
    }

    public static ChatDatabase getChatDatabase() {
        return chatDatabase;
    }

    public static void setChatDatabase(ChatDatabase chatDatabase) {
        CubesideClientFabric.chatDatabase = chatDatabase;
    }
}
