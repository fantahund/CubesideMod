package de.fanta.cubeside.mixin;

import de.fanta.cubeside.Config;
import de.fanta.cubeside.CubesideClient;
import de.fanta.cubeside.data.Database;
import de.fanta.cubeside.util.ChatHudMethods;
import de.fanta.cubeside.util.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

@Mixin(ChatHud.class)
public abstract class MixinChatHud extends DrawableHelper implements ChatHudMethods {

    @Final
    @Shadow
    private MinecraftClient client;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow protected abstract void addMessage(Text message, int messageId, int timestamp, boolean refresh);

    @Shadow public abstract void addToMessageHistory(String message);

    private static final Database database = CubesideClient.getDatabase();

    private static final Date DATE = new Date();

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At("HEAD"), argsOnly = true)
    private net.minecraft.text.Text addTimestamp(net.minecraft.text.Text componentIn) {
        if(CubesideClient.getInstance().isLoadingMessages()) {
            CubesideClient.getInstance().messageQueue.add(componentIn);
            return LiteralText.EMPTY;
        }

        if (Config.autochat) {
            String s = componentIn.toString();
            String[] arr = s.split(" ");

            if (arr.length >= 47) {
                if ((arr[4].equals("color=gray,")) && (arr[28].equals("TextComponent{text='From")) && (arr[32].equals("color=light_purple,")) && (arr[46].equals("color=white,") || arr[46].equals("color=green,"))) {
                    if (CubesideClient.instance.hasPermission("cubeside.autochat")) {
                        if (client.player != null) {
                            client.player.sendChatMessage("/r " + Config.antwort);
                        }
                    } else {
                        ChatUtils.sendErrorMessage("AutoChat kannst du erst ab Staff benutzen!");
                    }
                }
            }
        }

        if (Config.afkPling) {
            String AFKMessage = componentIn.getString();
            if (AFKMessage.equals("* Du bist nun abwesend.")) {
                playAFKSound();
            }
        }

        if (Config.saveMessagestoDatabase) {
            if (client.getCurrentServerEntry() != null) {
                database.addMessage(componentIn, client.getCurrentServerEntry().address.toLowerCase());
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

    @Inject(method = "addToMessageHistory", at = @At("HEAD"), cancellable = true)
    private void addMessageHistory(String message, CallbackInfo ci) {
        if (Config.saveMessagestoDatabase) {
            if (client.getCurrentServerEntry() != null) {
                database.addCommand(message, client.getCurrentServerEntry().address.toLowerCase());
            }
        }
    }

    @Override
    public void addStoredChatMessage(Text message) {
        this.addMessage(message, 0, this.client.inGameHud.getTicks(), false);
    }

    @Override
    public void addStoredMessage(String message) {
        this.addToMessageHistory(message);
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

    public void playAFKSound() {
        new Thread(() -> {
            try {
                if (client.player != null) {
                    client.player.playSound(new SoundEvent(new Identifier("block.note_block.bell")), SoundCategory.PLAYERS, 20.0f, 1.5f);
                    Thread.sleep(5 * 50);
                    client.player.playSound(new SoundEvent(new Identifier("block.note_block.bell")), SoundCategory.PLAYERS, 20.0f, 1.0f);
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }).start();
    }
}

