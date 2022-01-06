package de.fanta.cubeside.mixin;

import de.fanta.cubeside.CubesideClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.List;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    private final long SAVE_INTERVAL = 10000;

    @Final
    @Shadow
    public GameOptions options;
    private long lastSaveTime = 0;

    @Inject(at = @At("HEAD"), method = "close")
    private void close(CallbackInfo info) {
        options.write();
        //saveConfig();
    }

    @Inject(at = @At("HEAD"), method = "setScreen")
    private void openScreen(Screen screen, CallbackInfo info) {
        if (screen instanceof GameMenuScreen && System.currentTimeMillis() - lastSaveTime > SAVE_INTERVAL) {
            //saveConfig();
            lastSaveTime = System.currentTimeMillis();
        }

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
}
