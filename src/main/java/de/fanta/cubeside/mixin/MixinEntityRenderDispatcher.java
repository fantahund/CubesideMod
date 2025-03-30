package de.fanta.cubeside.mixin;

import de.fanta.cubeside.config.Configs;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
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
}
