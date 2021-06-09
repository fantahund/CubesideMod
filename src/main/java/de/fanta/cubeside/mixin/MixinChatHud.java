package de.fanta.cubeside.mixin;

import de.fanta.cubeside.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

@Mixin(ChatHud.class)
public abstract class MixinChatHud {

    @Final
    @Shadow
    private MinecraftClient client;

    private static final Date DATE = new Date();

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At("HEAD"), argsOnly = true)
    private net.minecraft.text.Text addTimestamp(net.minecraft.text.Text componentIn) {
        if (Config.autochat) {
            String s = componentIn.toString();
            String[] arr = s.split(" ");
            if (arr.length >= 47) {
                if ((arr[4].equals("color=gray,")) && (arr[28].equals("TextComponent{text='From")) && (arr[32].equals("color=light_purple,")) && (arr[46].equals("color=white,") || arr[46].equals("color=green,"))) {
                    client.player.sendChatMessage("/r " + Config.antwort);
                }
            }
        }


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

