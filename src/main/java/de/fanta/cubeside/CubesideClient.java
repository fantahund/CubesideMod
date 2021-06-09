package de.fanta.cubeside;

import de.fanta.cubeside.event.RankDataChannelHandler;
import de.fanta.cubeside.util.ChatUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import static java.lang.Double.POSITIVE_INFINITY;

@Environment(EnvType.CLIENT)
public class CubesideClient implements ClientModInitializer {
    public static final String MODID = "cubeside";
    public static final String PREFIX = "§9[§aCubeside§9] ";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    //GAMA
    public static double minGamma = -1.5;
    public static double maxGamma = 12.0;
    public static double prevGamma = POSITIVE_INFINITY;

    private ChatUtil chatUtil;
    private Commands commands;


    @Override
    public void onInitializeClient() {
        Config.deserialize();
        chatUtil = new ChatUtil();
        Events events = new Events();
        events.init();
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
}
