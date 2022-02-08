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
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
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

import java.awt.*;
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

    @Shadow
    protected abstract void addMessage(Text message, int messageId, int timestamp, boolean refresh);

    @Shadow
    public abstract void addToMessageHistory(String message);

    @Shadow
    protected abstract void removeMessage(int messageId);

    private static final Database database = CubesideClient.getDatabase();

    private static final Date DATE = new Date();

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At("HEAD"), argsOnly = true)
    private net.minecraft.text.Text addTimestamp(net.minecraft.text.Text componentIn) {
        if (CubesideClient.getInstance().isLoadingMessages()) {
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

        if (Config.clickabletpamessage) {
            String tpamessage = componentIn.getString();
            String[] args2 = tpamessage.split(" ", 2);
            String[] args5 = tpamessage.split(" ", 5);
            String[] args6 = tpamessage.split(" ", 6);
            net.minecraft.text.LiteralText component = new LiteralText("");
            if (args2.length == 2) {
                LiteralText name = new net.minecraft.text.LiteralText(args2[0]);
                name.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff592")));
                LiteralText accept = new net.minecraft.text.LiteralText("[Annehmen]");
                accept.setStyle(Style.EMPTY.withColor(TextColor.parse("#119e1d")).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept")));
                LiteralText deny = new net.minecraft.text.LiteralText(" [Ablehnen]");
                deny.setStyle(Style.EMPTY.withColor(TextColor.parse("#9e1139")).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny")));

                if (args2[1].startsWith("fragt, ob er sich zu dir teleportieren darf.")) {
                    component.append(name);
                    LiteralText message = new net.minecraft.text.LiteralText(" möchte sich zu dir teleportieren.\n");
                    message.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                    component.append(message);
                    component.append(accept);
                    component.append(deny);

                    if (Config.tpasound) {
                        if (client.player != null) {
                            client.player.playSound(new SoundEvent(new Identifier("block.note_block.flute")), SoundCategory.PLAYERS, 20.0f, 0.5f);
                        }
                    }

                    componentIn = component;
                }

                if (args2[1].startsWith("fragt, ob du dich zu ihm teleportieren möchtest.")) {
                    component.append(name);
                    LiteralText message = new net.minecraft.text.LiteralText(" möchte, dass du dich zu ihm teleportierst.\n");
                    message.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                    component.append(message);
                    component.append(accept);
                    component.append(deny);

                    if (Config.tpasound) {
                        if (client.player != null) {
                            client.player.playSound(new SoundEvent(new Identifier("block.note_block.flute")), SoundCategory.PLAYERS, 20.0f, 0.5f);
                        }
                    }

                    componentIn = component;
                }

                if (args2[1].startsWith("hat deine Teleportierungsanfrage angenommen.")) {
                    component.append(name);
                    LiteralText message = new net.minecraft.text.LiteralText(" hat deine Teleportierungsanfrage");
                    message.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                    component.append(message);
                    LiteralText message2 = new net.minecraft.text.LiteralText(" angenommen.");
                    message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#119e1d")));
                    component.append(message2);
                    componentIn = component;
                }

                if (args2[1].startsWith("hat deine Teleportierungsanfrage abgelehnt.")) {
                    component.append(name);
                    LiteralText message = new net.minecraft.text.LiteralText(" hat deine Teleportierungsanfrage");
                    message.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                    component.append(message);
                    LiteralText message2 = new net.minecraft.text.LiteralText(" abgelehnt.");
                    message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#9e1139")));
                    component.append(message2);
                    componentIn = component;
                }
            }
            if (args5.length == 5) {
                LiteralText name = new net.minecraft.text.LiteralText(args6[4].replace(".", ""));
                name.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff592")));

                if (tpamessage.startsWith("Du teleportierst dich zu")) {
                    LiteralText message1 = new net.minecraft.text.LiteralText("Du wirst zu ");
                    message1.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                    component.append(message1);
                    component.append(name);
                    LiteralText message2 = new net.minecraft.text.LiteralText(" teleportiert.");
                    message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                    component.append(message2);
                    componentIn = component;
                }
            }

            if (args6.length == 6) {
                LiteralText name = new net.minecraft.text.LiteralText(args6[4].replace(".", ""));
                name.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff592")));
                if (tpamessage.startsWith("Eine Anfrage wurde an")) {
                    LiteralText message1 = new net.minecraft.text.LiteralText("Du hast eine Anfrage an ");
                    message1.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                    component.append(message1);
                    component.append(name);
                    LiteralText message2 = new net.minecraft.text.LiteralText(" gesendet.");
                    message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                    component.append(message2);
                    componentIn = component;
                }

                if (tpamessage.startsWith("Diese Anfrage wird nach")) {
                    LiteralText message1 = new net.minecraft.text.LiteralText("Diese Anfrage wird in ");
                    message1.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                    component.append(message1);
                    component.append(name);
                    LiteralText seconds = new net.minecraft.text.LiteralText(" Sekunden ");
                    seconds.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff592")));
                    component.append(seconds);
                    LiteralText message2 = new net.minecraft.text.LiteralText("ablaufen.");
                    message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                    component.append(message2);
                    componentIn = component;
                }

            }
            if (tpamessage.equals("Teleportation läuft...")) {
                LiteralText message = new net.minecraft.text.LiteralText("Teleportation läuft...");
                message.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                component.append(message);
                componentIn = component;
            }

            if (tpamessage.equals("Du hast die Teleportierungsanfrage abgelehnt.")) {
                LiteralText message1 = new net.minecraft.text.LiteralText("Du hast die Teleportierungsanfrage");
                message1.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                component.append(message1);
                LiteralText message2 = new net.minecraft.text.LiteralText(" abgelehnt.");
                message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#9e1139")));
                component.append(message2);
                componentIn = component;
            }

            if (tpamessage.equals("Du hast die Teleportierungsanfrage angenommen.")) {
                LiteralText message1 = new net.minecraft.text.LiteralText("Du hast die Teleportierungsanfrage");
                message1.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                component.append(message1);
                LiteralText message2 = new net.minecraft.text.LiteralText(" angenommen.");
                message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#119e1d")));
                component.append(message2);
                componentIn = component;
            }

            if (tpamessage.equals("Fehler: Du hast keine Teleportierungsanfragen.")) {
                LiteralText message = new net.minecraft.text.LiteralText("Fehler: ");
                message.setStyle(Style.EMPTY.withColor(TextColor.parse("#9e1139")));
                component.append(message);
                LiteralText message2 = new net.minecraft.text.LiteralText("Du hast keine Teleportierungsanfrage.");
                message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                component.append(message2);
                componentIn = component;
            }
        }


        if (Config.chattimestamps) {
            net.minecraft.text.LiteralText component = new LiteralText("");
            LiteralText timestamp = new net.minecraft.text.LiteralText(getChatTimestamp() + " ");
            timestamp.setStyle(Style.EMPTY.withColor(Config.timestampColor));
            component.append(timestamp);
            component.append(componentIn);
            addMessagetoDatabase(component);
            return component;
        } else {
            addMessagetoDatabase(componentIn);
        }

        return componentIn;
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    private void addMessage(Text message, CallbackInfo ci) {
        if (Config.clickabletpamessage) {
            if (message.getString().startsWith("Du kannst diese Anfrage mit /tpdeny ablehnen.") || message.getString().startsWith("Du kannst die Teleportationsanfrage mit /tpaccept annehmen.") || message.getString().startsWith("Du kannst die Anfrage mit /tpacancel ablehnen.")) {
                ci.cancel();
            }
        }
    }


    @Inject(method = "addToMessageHistory", at = @At("HEAD"))
    private void addMessageHistory(String message, CallbackInfo ci) {
        if (CubesideClient.getInstance().isLoadingMessages()) {
            return;
        }
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
    public void addStoredCommand(String message) {
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

    public void addMessagetoDatabase(Text component) {
        if (Config.saveMessagestoDatabase) {
            if (client.getCurrentServerEntry() != null) {
                database.addMessage(component, client.getCurrentServerEntry().address.toLowerCase());
            }
        }
    }
}

