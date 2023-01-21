package de.fanta.cubeside.mixin;

import de.fanta.cubeside.ChatInfoHud;
import de.fanta.cubeside.config.Configs;
import de.fanta.cubeside.util.FlashColorScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    private static ChatInfoHud chatInfoHud;

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

    @Inject(method="render", at=@At(value="FIELD", target="Lnet/minecraft/client/option/GameOptions;debugEnabled:Z", opcode = Opcodes.GETFIELD, args = {"log=false"}))
    private void beforeRenderDebugScreen2(MatrixStack stack, float f, CallbackInfo ci) {
        chatInfoHud = chatInfoHud != null ? chatInfoHud : new ChatInfoHud();
        chatInfoHud.onRenderGameOverlayPost(stack);
        FlashColorScreen.onClientTick(stack);
    }
}
