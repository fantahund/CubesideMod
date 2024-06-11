package de.fanta.cubeside.mixin;

import de.fanta.cubeside.config.Configs;
import de.fanta.cubeside.util.FlashColorScreen;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    private Text overlayMessage;

    @Redirect(method = "renderOverlayMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I", ordinal = 0))
    public int render(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int color) {
        int n = textRenderer.getWidth(this.overlayMessage);
        return instance.drawText(textRenderer, this.overlayMessage, -n / 2, -4, color, Configs.Generic.ActionBarShadow.getBooleanValue());
    }

    @Inject(method = "renderMainHud", at = @At(value = "RETURN", opcode = Opcodes.GETFIELD, args = {"log=false"}))
    private void beforeRenderDebugScreen(DrawContext context, float tickDelta, CallbackInfo ci) {
        FlashColorScreen.onClientTick(context);
    }
}
