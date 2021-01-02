package de.fanta.cubeside.mixin;

import de.fanta.cubeside.Config;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.objectweb.asm.Opcodes;
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
            net.minecraft.text.LiteralText component = new LiteralText("");
            LiteralText timestamp = new net.minecraft.text.LiteralText(getChatTimestamp()+" ");
            timestamp.setStyle(Style.EMPTY.withColor(Config.timestampColor));
            component.append(timestamp);
            component.append(componentIn);
            return component;
        }
        return componentIn;
    }

    private static String getChatTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        DATE.setTime(System.currentTimeMillis());
        return sdf.format(DATE);
    }
}
