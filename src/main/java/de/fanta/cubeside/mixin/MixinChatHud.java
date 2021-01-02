package de.fanta.cubeside.mixin;

import com.sun.org.apache.xpath.internal.compiler.OpCodes;
import de.fanta.cubeside.Config;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Mixin(net.minecraft.client.gui.hud.ChatHud.class)
public abstract class MixinChatHud {

    private static final Date DATE = new Date();

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At("HEAD"), argsOnly = true)
    private net.minecraft.text.Text addTimestamp(net.minecraft.text.Text componentIn) {
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

    @ModifyConstant(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", constant = {@Constant(intValue = 100)})
    private int replaceMessageLimit(int original) {
        return Config.chatMessageLimit;
    }

    private static String getChatTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        DATE.setTime(System.currentTimeMillis());
        return sdf.format(DATE);
    }
}
