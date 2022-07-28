package de.fanta.cubeside.mixin;

import de.fanta.cubeside.config.Configs;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    private Text overlayMessage;

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I", ordinal = 0))
    public int render(TextRenderer instance, MatrixStack matrices, Text text, float x, float y, int color) {
        if (!Configs.Generic.ActionBarShadow.getBooleanValue()) {
            return getTextRenderer().draw(matrices, this.overlayMessage, x, -4.0F, color);
        }
        return getTextRenderer().drawWithShadow(matrices, this.overlayMessage, x, -4.0F, color);
    }
}
