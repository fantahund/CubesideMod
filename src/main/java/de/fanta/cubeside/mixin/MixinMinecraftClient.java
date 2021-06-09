package de.fanta.cubeside.mixin;

import de.fanta.cubeside.Config;
import de.fanta.cubeside.CubesideClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.List;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Final
    @Shadow
    public GameOptions options;

    @Inject(at = @At("HEAD"), method = "close")
    private void close(CallbackInfo info) {
        options.write();
        //saveConfig();
    }

    @Inject(at = @At("HEAD"), method = "openScreen")
    private void openScreen(Screen screen, CallbackInfo info) {
        if (screen != null && screen.getClass().getSimpleName().equals("SodiumOptionsGUI")) {
            try {
                List<?> optionPages = (List<?>) get(screen, "me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI", "pages");
                List<?> optionGroups = (List<?>) get(optionPages.get(0), "me.jellysquid.mods.sodium.client.gui.options.OptionPage", "groups");
                List<?> options = (List<?>) get(optionGroups.get(0), "me.jellysquid.mods.sodium.client.gui.options.OptionGroup", "options");
                Object sliderControl = get(options.get(1), "me.jellysquid.mods.sodium.client.gui.options.OptionImpl", "control");
                Class<?> sliderControlClass = Class.forName("me.jellysquid.mods.sodium.client.gui.options.control.SliderControl");
                setInt(sliderControl, sliderControlClass, "min", (int) (CubesideClient.minGamma * 100));
                setInt(sliderControl, sliderControlClass, "max", (int) (CubesideClient.maxGamma * 100));
            } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException ex) {
                ex.printStackTrace();
                CubesideClient.LOGGER.warn("an exception occurred during the manipulation of the sodium options gui");
            }
        }
    }

    private Object get(Object instance, String className, String name) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        Field f = Class.forName(className).getDeclaredField(name);
        f.setAccessible(true);
        return f.get(instance);
    }

    private void setInt(Object instance, Class<?> clazz, String field, int value) throws NoSuchFieldException, IllegalAccessException {
        Field f = clazz.getDeclaredField(field);
        f.setAccessible(true);
        f.setInt(instance, value);
    }

    @Redirect(method = "openScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;clear(Z)V"))
    private void toggleClearChat(ChatHud chatHud, boolean clear) {
        if (Config.clearchatbydisconnect) {
            chatHud.clear(true);
        }
    }
}
