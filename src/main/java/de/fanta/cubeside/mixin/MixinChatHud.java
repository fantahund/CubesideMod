package de.fanta.cubeside.mixin;

import de.fanta.cubeside.CubesideClientFabric;
import de.fanta.cubeside.config.Configs;
import de.fanta.cubeside.data.Database;
import de.fanta.cubeside.util.ChatHudMethods;
import de.fanta.cubeside.util.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

@Mixin(ChatHud.class)
public abstract class MixinChatHud extends DrawableHelper implements ChatHudMethods {

    private static final Database database = CubesideClientFabric.getDatabase();
    private static final Date DATE = new Date();
    @Final
    @Shadow
    private MinecraftClient client;

    private static String getChatTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        DATE.setTime(System.currentTimeMillis());
        return sdf.format(DATE);
    }

    @Shadow
    protected abstract void addMessage(Text message, @Nullable MessageSignatureData signature, int ticks, @Nullable MessageIndicator indicator, boolean refresh);

    @Redirect(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;logChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/client/gui/hud/MessageIndicator;)V"))
    private void addMessage(ChatHud instance, Text message, MessageIndicator indicator) {
        if (!CubesideClientFabric.isLoadingMessages()) {
            logChatMessage(message, indicator);
        }
    }

    @Shadow
    public abstract void addToMessageHistory(String message);

    @Shadow
    protected abstract void logChatMessage(Text message, @Nullable MessageIndicator indicator);

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"), argsOnly = true)
    private Text modifyMessages(Text componentIn) {
        if (CubesideClientFabric.isLoadingMessages()) {
            CubesideClientFabric.messageQueue.add(componentIn);
            return Text.empty();
        }
        if (Configs.PermissionSettings.AutoChat.getBooleanValue()) {
            String s = componentIn.toString();
            String[] arr = s.split(" ");

            if (arr.length >= 13) {
                if ((arr[4].equals("literal{From")) && (arr[5].equals("}[style={color=light_purple}],")) && (arr[13].contains("color=white") || arr[13].equals("color=green"))) {
                    if (client.player != null) {
                        if (CubesideClientFabric.hasPermission("cubeside.autochat")) {
                            client.player.sendMessage(Text.literal("r " + Configs.PermissionSettings.AutoChatAntwort.getStringValue()));
                        } else {
                            ChatUtils.sendErrorMessage("AutoChat kannst du erst ab Staff benutzen!");
                        }
                    }

                }
            }
        }

        if (Configs.Generic.AFKPling.getBooleanValue()) {
            String AFKMessage = componentIn.getString();
            if (AFKMessage.equals("* Du bist nun abwesend.")) {
                playAFKSound();
            }
        }

        if (Configs.Generic.ClickableTpaMessage.getBooleanValue()) {
            String tpamessage = componentIn.getString();
            String[] args2 = tpamessage.split(" ", 2);
            String[] args5 = tpamessage.split(" ", 5);
            String[] args6 = tpamessage.split(" ", 6);
            MutableText component = Text.literal("");
            if (args2.length == 2) {
                MutableText name = Text.literal(args2[0]);
                name.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff592")));
                MutableText accept = Text.literal("[Annehmen]");
                accept.setStyle(Style.EMPTY.withColor(TextColor.parse("#119e1d")).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept")));
                MutableText deny = Text.literal(" [Ablehnen]");
                deny.setStyle(Style.EMPTY.withColor(TextColor.parse("#9e1139")).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny")));

                if (args2[1].startsWith("fragt, ob er sich zu dir teleportieren darf.")) {
                    component.append(name);
                    MutableText message = Text.literal(" möchte sich zu dir teleportieren.\n");
                    message.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                    component.append(message);
                    component.append(accept);
                    component.append(deny);

                    if (Configs.Generic.TpaSound.getBooleanValue()) {
                        if (client.player != null) {
                            client.player.playSound(SoundEvent.of(new Identifier("block.note_block.flute")), SoundCategory.PLAYERS, 20.0f, 0.5f);
                        }
                    }

                    componentIn = component;
                }

                if (args2[1].startsWith("fragt, ob du dich zu ihm teleportieren möchtest.")) {
                    component.append(name);
                    MutableText message = Text.literal(" möchte, dass du dich zu ihm teleportierst.\n");
                    message.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                    component.append(message);
                    component.append(accept);
                    component.append(deny);

                    if (Configs.Generic.TpaSound.getBooleanValue()) {
                        if (client.player != null) {
                            client.player.playSound(SoundEvent.of(new Identifier("block.note_block.flute")), SoundCategory.PLAYERS, 20.0f, 0.5f);
                        }
                    }

                    componentIn = component;
                }

                if (args2[1].startsWith("hat deine Teleportierungsanfrage angenommen.")) {
                    component.append(name);
                    MutableText message = Text.literal(" hat deine Teleportierungsanfrage");
                    message.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                    component.append(message);
                    MutableText message2 = Text.literal(" angenommen.");
                    message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#119e1d")));
                    component.append(message2);
                    componentIn = component;
                }

                if (args2[1].startsWith("hat deine Teleportierungsanfrage abgelehnt.")) {
                    component.append(name);
                    MutableText message = Text.literal(" hat deine Teleportierungsanfrage");
                    message.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                    component.append(message);
                    MutableText message2 = Text.literal(" abgelehnt.");
                    message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#9e1139")));
                    component.append(message2);
                    componentIn = component;
                }
            }
            if (args5.length == 5) {
                MutableText name = Text.literal(args6[4].replace(".", ""));
                name.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff592")));

                if (tpamessage.startsWith("Du teleportierst dich zu")) {
                    MutableText message1 = Text.literal("Du wirst zu ");
                    message1.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                    component.append(message1);
                    component.append(name);
                    MutableText message2 = Text.literal(" teleportiert.");
                    message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                    component.append(message2);
                    componentIn = component;
                }
            }

            if (args6.length == 6) {
                MutableText name = Text.literal(args6[4].replace(".", ""));
                name.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff592")));
                if (tpamessage.startsWith("Eine Anfrage wurde an")) {
                    MutableText message1 = Text.literal("Du hast eine Anfrage an ");
                    message1.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                    component.append(message1);
                    component.append(name);
                    MutableText message2 = Text.literal(" gesendet.");
                    message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                    component.append(message2);
                    componentIn = component;
                }

                if (tpamessage.startsWith("Diese Anfrage wird nach")) {
                    MutableText message1 = Text.literal("Diese Anfrage wird in ");
                    message1.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                    component.append(message1);
                    component.append(name);
                    MutableText seconds = Text.literal(" Sekunden ");
                    seconds.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff592")));
                    component.append(seconds);
                    MutableText message2 = Text.literal("ablaufen.");
                    message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                    component.append(message2);
                    componentIn = component;
                }

            }
            if (tpamessage.equals("Teleportation läuft...")) {
                MutableText message = Text.literal("Teleportation läuft...");
                message.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                component.append(message);
                componentIn = component;
            }

            if (tpamessage.equals("Du hast die Teleportierungsanfrage abgelehnt.")) {
                MutableText message1 = Text.literal("Du hast die Teleportierungsanfrage");
                message1.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                component.append(message1);
                MutableText message2 = Text.literal(" abgelehnt.");
                message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#9e1139")));
                component.append(message2);
                componentIn = component;
            }

            if (tpamessage.equals("Du hast die Teleportierungsanfrage angenommen.")) {
                MutableText message1 = Text.literal("Du hast die Teleportierungsanfrage");
                message1.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                component.append(message1);
                MutableText message2 = Text.literal(" angenommen.");
                message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#119e1d")));
                component.append(message2);
                componentIn = component;
            }

            if (tpamessage.equals("Fehler: Du hast keine Teleportierungsanfragen.")) {
                MutableText message = Text.literal("Fehler: ");
                message.setStyle(Style.EMPTY.withColor(TextColor.parse("#9e1139")));
                component.append(message);
                MutableText message2 = Text.literal("Du hast keine Teleportierungsanfrage.");
                message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db")));
                component.append(message2);
                componentIn = component;
            }
        }


        if (Configs.Chat.ChatTimeStamps.getBooleanValue()) {
            MutableText component = Text.literal("");
            MutableText timestamp = Text.literal(getChatTimestamp() + " ");
            timestamp.setStyle(Style.EMPTY.withColor(Configs.Chat.TimeStampColor.getColor().intValue));
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
        if (Configs.Generic.ClickableTpaMessage.getBooleanValue()) {
            if (message.getString().equals("Du kannst diese Anfrage mit /tpdeny ablehnen.") || message.getString().equals("Du kannst die Teleportationsanfrage mit /tpaccept annehmen.") || message.getString().equals("Du kannst die Anfrage mit /tpacancel ablehnen.")) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "addToMessageHistory", at = @At("HEAD"))
    private void addMessageHistory(String message, CallbackInfo ci) {
        if (CubesideClientFabric.isLoadingMessages()) {
            return;
        }
        if (Configs.Chat.SaveMessagesToDatabase.getBooleanValue()) {
            if (client.getCurrentServerEntry() != null) {
                if (!CubesideClientFabric.databaseinuse) {
                    database.addCommand(message, client.getCurrentServerEntry().address.toLowerCase());
                }
            }
        }
    }

    @Override
    public void addStoredChatMessage(Text message) {
        this.addMessage(message, null, 0, new MessageIndicator(10631423, null, null, null), false);
    }

    @Override
    public void addStoredCommand(String message) {
        this.addToMessageHistory(message);
    }

    @ModifyConstant(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", constant = {@Constant(intValue = 100)})
    private int replaceMessageLimit(int original) {
        return Configs.Chat.ChatMessageLimit.getIntegerValue();
    }

    public void playAFKSound() {
        new Thread(() -> {
            try {
                if (client.player != null) {
                    client.player.playSound(SoundEvent.of(new Identifier("block.note_block.bell")), SoundCategory.PLAYERS, 20.0f, 1.5f);
                    Thread.sleep(5 * 50);
                    client.player.playSound(SoundEvent.of(new Identifier("block.note_block.bell")), SoundCategory.PLAYERS, 20.0f, 1.0f);
                }
            } catch (Exception e) {
                CubesideClientFabric.LOGGER.error(e);
            }
        }).start();
    }

    public void addMessagetoDatabase(Text component) {
        if (Configs.Chat.SaveMessagesToDatabase.getBooleanValue()) {
            if (client.getCurrentServerEntry() != null) {
                if (!CubesideClientFabric.databaseinuse) {
                    database.addMessage(component, client.getCurrentServerEntry().address.toLowerCase());
                }
            }
        }
    }
}

