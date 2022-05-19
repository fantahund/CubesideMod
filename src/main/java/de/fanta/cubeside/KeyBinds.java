package de.fanta.cubeside;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {
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
}
