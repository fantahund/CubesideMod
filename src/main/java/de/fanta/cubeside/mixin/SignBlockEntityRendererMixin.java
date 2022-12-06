package de.fanta.cubeside.mixin;

import de.fanta.cubeside.config.Configs;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.text.OrderedText;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SignBlockEntityRenderer.class)
class SignBlockEntityRendererMixin {

    @Shadow
    @Final
    private TextRenderer textRenderer;

    @Redirect(method = "renderText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithOutline(Lnet/minecraft/text/OrderedText;FFIILorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    public void renderOldGlowText(TextRenderer textRenderer, OrderedText text, float x, float y, int color, int outlineColor, Matrix4f matrix, VertexConsumerProvider vertexConsumers, int light) {
        if (Configs.Fixes.SimpleSignGlow.getBooleanValue()) {
            this.textRenderer.draw(text, x, y, color, false, matrix, vertexConsumers, false, 0, light);
        } else {
            this.textRenderer.drawWithOutline(text, x, y, color, outlineColor, matrix, vertexConsumers, light);
        }
    }

}
