package de.fanta.cubeside.mixin;

import de.fanta.cubeside.CubesideClientFabric;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "enterReconfiguration(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At(value = "HEAD"))
    public void clearClientLevelHead(final CallbackInfo ci) {
        CubesideClientFabric.setInClearLevel(true);
    }

    @Inject(method = "enterReconfiguration(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At(value = "TAIL"))
    public void clearClientLevelTail(final CallbackInfo ci) {
        CubesideClientFabric.setInClearLevel(false);
    }
}
