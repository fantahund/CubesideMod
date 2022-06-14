package de.fanta.cubeside;

import de.fanta.cubeside.data.Database;
import de.fanta.cubeside.event.RankDataChannelHandler;
import de.fanta.cubeside.permission.PermissionHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.POSITIVE_INFINITY;

@Environment(EnvType.CLIENT)
public class CubesideClientFabric implements ClientModInitializer {

    public static final String MODID = "cubeside";
    public static final String PREFIX = "§9[§aCubeside§9] ";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    private static Database DATABASE;
    //GAMA
    public static double minGamma = -1.0;
    public static double maxGamma = 12.0;
    public static double prevGamma = POSITIVE_INFINITY;

    public static Commands commands;
    private static PermissionHandler permissionHandler;

    private static String rank;

    private static boolean loadingMessages;
    public static boolean databaseinuse = false;
    public static List<Text> messageQueue = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        try {
            DATABASE = new Database();
        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Du hast scheinbar mehrere Minecraft Instanzen am laufen. Chat & Commands werden nicht gespeichert oder geladen!");
            databaseinuse = true;
        }

        Config.deserialize();

        KeyBinds keyBinds = new KeyBinds();
        keyBinds.initKeys();

        Events events = new Events();
        events.init();

        LogicalZoom logicalZoom = new LogicalZoom();
        logicalZoom.initLogicalZoom();

        permissionHandler = new PermissionHandler();
        new RankDataChannelHandler();

        LOGGER.info(MODID + "Mod Loaded");
        commands = new Commands();

        if (!databaseinuse) {
            try {
                getDatabase().deleteOldMessages(Config.daystheMessagesareStored);
                getDatabase().deleteOldCommands(Config.daystheMessagesareStored);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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
}
