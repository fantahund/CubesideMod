package de.fanta.cubeside.mixin;

import de.fanta.cubeside.config.Configs;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlockEntityRenderer.class)
public abstract class MixinRemoveChristmasChest {

    @Inject(method = "isAroundChristmas", at = @At("TAIL"), cancellable = true)
    private static void setChristmas(CallbackInfoReturnable<Boolean> cir) {
        if (Configs.Fun.DisableChristmasChest.getBooleanValue()) {
            cir.setReturnValue(false);
        }
    }
}
