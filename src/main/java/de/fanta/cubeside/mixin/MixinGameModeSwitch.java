package de.fanta.cubeside.mixin;

import de.fanta.cubeside.util.ChatUtils;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameModeSelectionScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Keyboard.class)
public class MixinGameModeSwitch {

    @Inject(at = @At("HEAD"), method = "processF3(I)Z", cancellable = true)
    public void behaviour(int key, CallbackInfoReturnable<Boolean> cir) {
        if (key == 293) {
            MinecraftClient.getInstance().setScreen(new GameModeSelectionScreen());
        }
    }
}
