package de.fanta.cubeside;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {
    public static KeyBinding NARRATOR_KEYBINDING;
    public static KeyBinding AUTO_CHAT;
    //public static final KeyBinding TOGGLE_GAMA
    public static KeyBinding TOGGLE_SHOW_ENTITIES_IN_SPECTATOR_MODE;

    public void initKeys() {
        NARRATOR_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "options.narrator",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "key.categories.misc"
        ));

        AUTO_CHAT = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cubeside.autochat",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "key.categories.cubeside"
        ));

        /*TOGGLE_GAMA = KeyBindingHelper.registerKeyBinding(new KeyBinding( //TODO Minecraft has prevented in the 1.19 gamma waiting for team meeting result!
                "key.cubeside.gama",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "key.categories.cubeside"
        ));*/

        TOGGLE_SHOW_ENTITIES_IN_SPECTATOR_MODE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cubeside.showentitiesinspectator",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "key.categories.cubeside"
        ));
    }

}
