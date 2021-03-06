package de.fanta.cubeside;

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

@Environment(EnvType.CLIENT)
public class CubesideClient implements ClientModInitializer {
    public static final String MODID = "cubeside";
    public static final String PREFIX = "§9[§aCubeside§9] ";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    private ChatUtil chatUtil;
    private Events events;




    @Override
    public void onInitializeClient() {
        Config.deserialize();
        chatUtil = new ChatUtil();
        events = new Events();
        events.init();

        LOGGER.info(MODID + "Mod Loaded");

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
            "key.categories.cubeide"
    ));
}
