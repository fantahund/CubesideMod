package de.fanta.cubeside;

import de.fanta.cubeside.config.Configs;
import de.fanta.cubeside.data.Database;
import de.fanta.cubeside.event.CubesideModChannelHandler;
import de.fanta.cubeside.event.RankDataChannelHandler;
import de.fanta.cubeside.permission.PermissionHandler;
import de.fanta.cubeside.util.ChatInfo;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CubesideClientFabric implements ClientModInitializer {

    public static final String MODID = "cubeside";
    public static final String PREFIX = "§9[§aCubeside§9] ";
    public static final Logger LOGGER = LogManager.getLogger("CubesideMod");
    private static Database DATABASE;
    //GAMA
    public static double minGamma = -1.5;
    public static double maxGamma = 12.0;

    public static double brightnessSliderInterval = 0.05;

    public static Commands commands;
    private static PermissionHandler permissionHandler;

    private static String rank;

    private static boolean loadingMessages;
    public static boolean databaseinuse = false;
    public static List<Text> messageQueue = new ArrayList<>();

    private static boolean xaeroFairPlay;

    private static long time;
    
    private static ChatInfo chatInfo;

    @Override
    public void onInitializeClient() {
        try {
            DATABASE = new Database();
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Du hast scheinbar mehrere Minecraft Instanzen am laufen. Chat & Commands werden nicht gespeichert oder geladen!");
            databaseinuse = true;
        }
        Configs.loadFromFile();

        KeyBinds keyBinds = new KeyBinds();
        keyBinds.initKeys();

        Events events = new Events();
        events.init();

        LogicalZoom logicalZoom = new LogicalZoom();
        logicalZoom.initLogicalZoom();

        permissionHandler = new PermissionHandler();
        new RankDataChannelHandler();
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
        this.restartTask(50);
    }

    public String getRank() {
        return rank;
    }

    public static void setRank(String setrank) {
        rank = setrank;
    }

    public static boolean hasPermission(String permission) {
        return permissionHandler.hasPermission(rank, permission);
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

    public void restartTask(long l) {
        Thread timer = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(l);
                    time++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        timer.start();
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
}
