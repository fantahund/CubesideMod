package de.fanta.cubeside;

import de.fanta.cubeside.data.Database;
import de.fanta.cubeside.event.RankDataChannelHandler;
import de.fanta.cubeside.permission.PermissionHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.POSITIVE_INFINITY;

@Environment(EnvType.CLIENT)
public class CubesideClient implements ClientModInitializer {
    public static CubesideClient instance;
    public static final String MODID = "cubeside";
    public static final String PREFIX = "§9[§aCubeside§9] ";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    private static final Database DATABASE = new Database();
    //GAMA
    public static double minGamma = -1.5;
    public static double maxGamma = 12.0;
    public static double prevGamma = POSITIVE_INFINITY;

    private Commands commands;
    private PermissionHandler permissionHandler;

    private String rank;

    private boolean loadingMessages;
    public List<Text> messageQueue = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        if (instance == null) {
            instance = this;
        }

        Config.deserialize();
        Events events = new Events();
        events.init();
        permissionHandler = new PermissionHandler();
        new RankDataChannelHandler();

        LOGGER.info(MODID + "Mod Loaded");
        commands = new Commands();
        commands.register();


    }

    public static final KeyBinding NARRATOR_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "options.narrator",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "key.categories.misc"
    ));

    public static final KeyBinding AUTO_CHAT = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.cubeside.autochat",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_ALT,
            "key.categories.cubeside"
    ));

    public static final KeyBinding TOGGLE_GAMA = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.cubeside.gama",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "key.categories.cubeside"
    ));

    public static final KeyBinding TOGGLE_SHOW_ENTITIES_IN_SPECTATOR_MODE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.cubeside.showentitiesinspectator",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.categories.cubeside"
    ));

    public static final KeyBinding WEIHNACHTSMARKT = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.cubeside.weihnachtsmarkt",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_U,
            "key.categories.cubeside"
    ));



    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public static CubesideClient getInstance() {
        return instance;
    }

    public boolean hasPermission(String permission) {
        return permissionHandler.hasPermission(rank, permission);
    }

    public static Database getDatabase() {
        return DATABASE;
    }

    public void setLoadingMessages(boolean loadingMessages) {
        this.loadingMessages = loadingMessages;
    }

    public boolean isLoadingMessages() {
        return loadingMessages;
    }
}
