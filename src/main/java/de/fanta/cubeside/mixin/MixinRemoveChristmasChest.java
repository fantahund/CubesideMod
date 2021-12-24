package de.fanta.cubeside.mixin;

import de.fanta.cubeside.Config;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlockEntityRenderer.class)
public abstract class MixinRemoveChristmasChest {
    @Shadow
    private boolean christmas;

    @Inject(method="<init>", at=@At("TAIL"))
    public void ChestBlockEntityRenderer(BlockEntityRendererFactory.Context ctx, CallbackInfo ci) {
        if (Config.removeChristmasChest) {
            this.christmas = false;
        }
    }
}
