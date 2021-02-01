package de.fanta.cubeside;

import de.fanta.cubeside.util.ChatUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.MinecraftClientGame;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.Perspective;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
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
}
