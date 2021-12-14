package de.fanta.cubeside.mixin;

import de.fanta.cubeside.Config;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetHandler {

    @Inject(method = "onUnloadChunk", at = {@At("HEAD")}, cancellable = true)
    private void onUnload(UnloadChunkS2CPacket packet, CallbackInfo ci) {
        if (!Config.unloadchunks) {
            ci.cancel();
        }
    }

    @Redirect(method = "onChunkLoadDistance", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/ChunkLoadDistanceS2CPacket;getDistance()I"))
    private int onViewDistChange(ChunkLoadDistanceS2CPacket instance) {
        if (Config.fullverticalview) {
            return Config.fakeviewdistance;
        }
        return instance.getDistance();
    }

    @Redirect(method = "onGameJoin", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/GameJoinS2CPacket;viewDistance()I"))
    private int onJoinGame(GameJoinS2CPacket instance) {
        if (Config.fullverticalview) {
            return Config.fakeviewdistance;
        }
        return instance.viewDistance();
    }
}
