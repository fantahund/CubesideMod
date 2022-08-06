package de.fanta.cubeside.mixin;

import de.fanta.cubeside.CubesideClientFabric;
import de.fanta.cubeside.config.Configs;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MessageHandler.class)
public abstract class MixinMessageHandler {

    @Shadow
    protected abstract void disconnect();

    @Redirect(method = "processHeader", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/message/MessageHandler;disconnect()V"))
    public void renderOldGlowText(MessageHandler instance) {
        if (Configs.Fixes.KickByNotValidMessage.getBooleanValue()) {
            this.disconnect();
        } else {
            CubesideClientFabric.LOGGER.log(Level.ERROR, Text.translatable("multiplayer.disconnect.chat_validation_failed").getString());
        }
    }
}
