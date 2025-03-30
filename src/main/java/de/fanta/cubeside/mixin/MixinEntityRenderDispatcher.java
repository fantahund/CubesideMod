package de.fanta.cubeside.mixin;

import de.fanta.cubeside.config.Configs;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.state.EntityHitboxAndView;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher {
    @Inject(at = @At("RETURN"), method = "setRenderHitboxes")
    public void setRenderHitboxes(boolean renderHitboxes, CallbackInfo ci) {
        Configs.HitBox.ShowHitBox.setBooleanValue(renderHitboxes);
        Configs.saveToFile();
    }

    @Inject(method = "renderHitboxes(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/entity/state/EntityHitboxAndView;Lnet/minecraft/client/render/VertexConsumer;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;<init>(DDD)V", ordinal = 0), cancellable = true)
    private static void conditionalVec3dCancel(MatrixStack matrices, EntityHitboxAndView hitbox, VertexConsumer vertexConsumer, float standingEyeHeight, CallbackInfo ci) {
        if (!Configs.HitBox.EntityHitBoxDirection.getBooleanValue()) {
            ci.cancel();
        }
    }
}
