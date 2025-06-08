package de.fanta.cubeside.mixin;

import de.fanta.cubeside.util.FlashColorScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    // FIXME 1.21.6 ActionBarShadow
    // @Redirect(method = "renderOverlayMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithBackground(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIII)I"))
    // public int render(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int width, int color) {
    // if (!Configs.Generic.ActionBarShadow.getBooleanValue()) {
    // return instance.drawText(textRenderer, text, x, y, color, false);
    // } else {
    // return instance.drawTextWithBackground(textRenderer, text, x, y, width, color);
    // }
    // }

    @Inject(method = "renderMainHud", at = @At(value = "RETURN", opcode = Opcodes.GETFIELD, args = { "log=false" }))
    private void beforeRenderDebugScreen(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        FlashColorScreen.onClientTick(context);
    }
}
