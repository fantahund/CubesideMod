package de.fanta.cubeside.mixin;

import de.fanta.cubeside.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.text.SimpleDateFormat;
import java.util.Date;

@Mixin(net.minecraft.client.gui.hud.ChatHud.class)
public abstract class MixinChatHud extends net.minecraft.client.gui.DrawableHelper {
    private static final Date DATE = new Date();
    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At("HEAD"), argsOnly = true)
    private net.minecraft.text.Text addTimestamp(net.minecraft.text.Text componentIn)
    {
        if (Config.chattimestamps) {
            net.minecraft.text.LiteralText newComponent = new net.minecraft.text.LiteralText(getChatTimestamp() + " ");
            newComponent.append(componentIn);
            return newComponent;
        }

        return componentIn;
    }

    private static String getChatTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        DATE.setTime(System.currentTimeMillis());
        return sdf.format(DATE);
    }
}
