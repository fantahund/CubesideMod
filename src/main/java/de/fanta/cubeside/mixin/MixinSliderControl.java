package de.fanta.cubeside.mixin;

import de.fanta.cubeside.CubesideClientFabric;
import me.jellysquid.mods.sodium.client.gui.options.Option;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlValueFormatter;
import me.jellysquid.mods.sodium.client.gui.options.control.SliderControl;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(SliderControl.class)
public class MixinSliderControl {


    @Mutable
    @Shadow @Final private int min;

    @Mutable
    @Shadow @Final private int max;

    @Mutable
    @Shadow @Final private int interval;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void init(Option option, int min, int max, int interval, ControlValueFormatter mode, CallbackInfo ci) {
        if (option.getName().getContent() instanceof TranslatableTextContent content && content.getKey().equals("options.gamma")) {
            this.min = (int) (CubesideClientFabric.minGamma * 100);
            this.max = (int) (CubesideClientFabric.maxGamma * 100);
            this.interval = (int) (CubesideClientFabric.brightnessSliderInterval * 100);
        }
    }
}
