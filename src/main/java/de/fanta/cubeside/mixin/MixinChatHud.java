package de.fanta.cubeside.mixin;

import de.fanta.cubeside.Config;
import de.fanta.cubeside.CubesideClient;
import de.fanta.cubeside.util.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Async;
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

    @Shadow
    @Final
    private static Logger LOGGER;
    private static final Date DATE = new Date();

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At("HEAD"), argsOnly = true)
    private net.minecraft.text.Text addTimestamp(net.minecraft.text.Text componentIn) {
        if (Config.autochat) {
            String s = componentIn.toString();
            String[] arr = s.split(" ");

            if (arr.length >= 47) {
                if ((arr[4].equals("color=gray,")) && (arr[28].equals("TextComponent{text='From")) && (arr[32].equals("color=light_purple,")) && (arr[46].equals("color=white,") || arr[46].equals("color=green,"))) {
                    if (CubesideClient.instance.hasPermission("cubeside.autochat")) {
                        client.player.sendChatMessage("/r " + Config.antwort);
                    } else {
                        ChatUtils.sendErrorMessage("AutoChat kannst du erst ab Staff benutzen!");
                    }
                }
            }

        }

        if (Config.afkPling) {
            String AFKMessage = componentIn.getString();
            if (AFKMessage.equals("* Du bist nun abwesend.")) {
                client.player.playSound(new SoundEvent(new Identifier("block.note_block.bell")), SoundCategory.PLAYERS, 20.0f, 1.5f);
                new Thread(() -> {
                    try {
                        Thread.sleep(5 * 50);
                        client.player.playSound(new SoundEvent(new Identifier("block.note_block.bell")), SoundCategory.PLAYERS, 20.0f, 1.0f);
                    } catch (Exception e) {
                        LOGGER.error(e);
                    }
                }).start();
            }
        }

        if (Config.chattimestamps) {
            net.minecraft.text.LiteralText component = new LiteralText("");
            LiteralText timestamp = new net.minecraft.text.LiteralText(getChatTimestamp() + " ");
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

    @Inject(method = "clear", at = @At(value = "HEAD"), cancellable = true)
    private void preventChatReset(boolean clearHistory, CallbackInfo ci) {
        if (!Config.clearchatbydisconnect) {
            ci.cancel();
        }
    }

    private static String getChatTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        DATE.setTime(System.currentTimeMillis());
        return sdf.format(DATE);
    }
}

