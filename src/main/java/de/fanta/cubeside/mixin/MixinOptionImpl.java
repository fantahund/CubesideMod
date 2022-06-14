package de.fanta.cubeside.mixin;

import me.jellysquid.mods.sodium.client.gui.options.OptionFlag;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpact;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.binding.GenericBinding;
import me.jellysquid.mods.sodium.client.gui.options.binding.OptionBinding;
import me.jellysquid.mods.sodium.client.gui.options.control.Control;
import me.jellysquid.mods.sodium.client.gui.options.storage.OptionStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.function.Function;

@Pseudo
@Mixin(OptionImpl.class)
public class MixinOptionImpl<S, T> {
    @Shadow
    @Final
    @Mutable
    private OptionBinding<S, T> binding;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void init(OptionStorage<S> storage,
                      Text name,
                      Text tooltip,
                      OptionBinding<S, T> binding,
                      Function<OptionImpl<S, T>, Control<T>> control,
                      EnumSet<OptionFlag> flags,
                      OptionImpact impact,
                      boolean enabled,
                      CallbackInfo info) {

        if (name.getContent() instanceof TranslatableTextContent content && content.getKey().equals("options.gamma")) {
            this.binding = new GenericBinding<S, T>((opt, val) -> MinecraftClient.getInstance().options.getGamma().setValue((Integer) val * 0.01D), binding::getValue);
        }
    }
}
