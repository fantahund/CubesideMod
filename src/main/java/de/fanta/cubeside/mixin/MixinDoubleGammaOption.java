package de.fanta.cubeside.mixin;

import de.fanta.cubeside.CubesideClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.DoubleOption;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.Math.abs;

@Mixin(DoubleOption.class)
public class MixinDoubleGammaOption {
    @Shadow
    @Final
    @Mutable
    private BiFunction<GameOptions, DoubleOption, Text> displayStringGetter;

    @Final
    @Shadow
    @Mutable
    protected double min;
    @Shadow
    @Mutable
    protected double max;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(String key, double min, double max, float step, Function<GameOptions, Double> getter, BiConsumer<GameOptions, Double> setter, BiFunction<GameOptions, DoubleOption, Text> displayStringGetter, Function<MinecraftClient, List<OrderedText>> tooltipsGetter, CallbackInfo ci) {
        if (key.equals("options.gamma")) {
            this.min = CubesideClient.minGamma;
            this.max = CubesideClient.maxGamma;
            this.displayStringGetter = this::displayStringGetter;
        }
    }

    private Text displayStringGetter(GameOptions gameOptions, DoubleOption doubleOption) {
        double threshold = 0.025;
        return new TranslatableText("options.gamma").append(": ").append(
                abs(gameOptions.gamma) < threshold ? new TranslatableText("options.gamma.min") :
                        abs(gameOptions.gamma - 1) < threshold ? new TranslatableText("options.gamma.max") :
                                new LiteralText(Math.round(gameOptions.gamma * 100) + "%"));
    }
}
