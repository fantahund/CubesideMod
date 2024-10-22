package de.fanta.cubeside.mixin;

import com.mojang.serialization.Codec;
import de.fanta.cubeside.util.BoostedSliderCallbacks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.SimpleOption.Callbacks;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(SimpleOption.class)
public class MixinGammaSimpleOption {
    @Shadow
    @Final
    Text text;

    @Shadow
    @Final
    @Mutable
    Function<Double, Text> textGetter;

    @Shadow
    @Final
    @Mutable
    Callbacks<Double> callbacks;

    @Shadow
    @Final
    @Mutable
    Codec<Double> codec;

    @Shadow
    @Final
    @Mutable
    Consumer<Double> changeCallback;

    @Inject(at = @At("RETURN"), method = "<init>(Ljava/lang/String;Lnet/minecraft/client/option/SimpleOption$TooltipFactory;Lnet/minecraft/client/option/SimpleOption$ValueTextGetter;Lnet/minecraft/client/option/SimpleOption$Callbacks;Lcom/mojang/serialization/Codec;Ljava/lang/Object;Ljava/util/function/Consumer;)V")
    private void init(CallbackInfo info) {
        TextContent content = this.text.getContent();
        if (!(content instanceof TranslatableTextContent translatableTextContent))
            return;

        String key = translatableTextContent.getKey();
        if (!key.equals("options.gamma"))
            return;

        this.textGetter = this::textGetter;
        this.callbacks = BoostedSliderCallbacks.INSTANCE;
        this.codec = this.callbacks.codec();
        this.changeCallback = this::changeCallback;
    }

    @Unique
    private Text textGetter(Double gamma) {
        long brightness = Math.round(gamma * 100);
        return Text.translatable("options.gamma").append(": ").append(
                brightness == 0 ? Text.translatable("options.gamma.min") :
                brightness == 100 ? Text.translatable("options.gamma.max") :
                Text.literal(String.valueOf(brightness)));
    }

    @Unique
    private void changeCallback(Double gamma) {
        MinecraftClient.getInstance().options.getGamma().setValue(gamma);
    }
}
