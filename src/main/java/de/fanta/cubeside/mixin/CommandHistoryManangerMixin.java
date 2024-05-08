package de.fanta.cubeside.mixin;

import de.fanta.cubeside.config.Configs;
import net.minecraft.client.util.CommandHistoryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;

@Mixin(CommandHistoryManager.class)
public class CommandHistoryManangerMixin {

    @Inject(method = "getHistory", at = @At(value = "FIELD", target = "Lnet/minecraft/client/util/CommandHistoryManager;history:Lnet/minecraft/util/collection/ArrayListDeque;"), cancellable = true)
    private void getHistory(CallbackInfoReturnable<Collection<String>> cir) {
        if (Configs.Chat.SaveMessagesToDatabase.getBooleanValue()) {
            cir.setReturnValue(new ArrayList<>());
        }
    }
}
