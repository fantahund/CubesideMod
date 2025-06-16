package de.fanta.cubeside.mixin;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import de.fanta.cubeside.ChatInfoHud;
import de.fanta.cubeside.CubesideClientFabric;
import de.fanta.cubeside.config.Configs;
import de.fanta.cubeside.data.ChatDatabase;
import de.fanta.cubeside.util.ChatHudMethods;
import de.fanta.cubeside.util.ChatUtils;
import de.iani.cubesideutils.fabric.permission.PermissionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryOps;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class MixinChatHud implements ChatHudMethods {
    @Unique
    private static final Date DATE = new Date();
    @Unique
    private Text lastMessage;
    @Unique
    private Text lastEditMessage;
    @Unique
    private int count = 1;
    @Unique
    private static ChatInfoHud chatInfoHud;
    @Final
    @Shadow
    private MinecraftClient client;

    @Unique
    private static String getChatTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss]");
        DATE.setTime(System.currentTimeMillis());
        return sdf.format(DATE);
    }

    @Redirect(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;logChatMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V"))
    private void addMessage(ChatHud instance, ChatHudLine message) {
        if (!CubesideClientFabric.isLoadingMessages()) {
            logChatMessage(message);
        }
    }

    @Shadow
    public abstract void addToMessageHistory(String message);

    @Shadow
    public abstract void logChatMessage(ChatHudLine message);

    @Shadow
    @Final
    public List<ChatHudLine.Visible> visibleMessages;

    @Shadow
    public abstract int getWidth();

    @Shadow
    public abstract double getChatScale();

    @Shadow
    @Final
    public List<ChatHudLine> messages;

    @Shadow
    public abstract void addVisibleMessage(ChatHudLine message);

    @Inject(method = "render", at = @At(value = "RETURN"))
    private void renderChatHudInfo(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        if (focused) {
            chatInfoHud = chatInfoHud != null ? chatInfoHud : new ChatInfoHud();
            chatInfoHud.onRenderChatInfoHud(context);
        }
    }

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"), argsOnly = true)
    private Text modifyMessages(Text componentIn) {
        if (CubesideClientFabric.isLoadingMessages()) {
            CubesideClientFabric.messageQueue.add(componentIn);
            return Text.empty();
        }

        if (Configs.Chat.CountDuplicateMessages.getBooleanValue()) {
            if (lastMessage != null && lastMessage.equals(componentIn)) {
                count++;
                lastMessage = componentIn;

                MutableText text = lastMessage.copy();
                MutableText countText = Text.literal(String.format(Configs.Chat.CountDuplicateMessagesFormat.getStringValue(), count));
                countText.setStyle(Style.EMPTY.withColor(TextColor.parse(Configs.Chat.CountDuplicateMessagesColor.getColor().toHexString()).result().get()));
                text.append(countText);
                componentIn = text;

                if (lastEditMessage != null) {
                    int with = MathHelper.floor(this.getWidth() / this.getChatScale());
                    List<OrderedText> list = ChatMessages.breakRenderedChatMessageLines(lastEditMessage, with, this.client.textRenderer);
                    for (int i = 1; i <= list.size(); i++) {
                        if (!this.visibleMessages.isEmpty()) {
                            this.visibleMessages.removeFirst();
                        }
                        if (!this.messages.isEmpty()) {
                            this.messages.removeFirst();
                        }
                    }
                    if (CubesideClientFabric.getChatDatabase() != null) {
                        try {
                            CubesideClientFabric.getChatDatabase().deleteNewestMessage();
                        } catch (Throwable e) {
                            CubesideClientFabric.LOGGER.log(Level.WARN, "Could not delete latest message from Database " + e.getMessage());
                        }
                    }
                }

            } else {
                lastMessage = componentIn;
                count = 1;
            }
        }

        if (Configs.PermissionSettings.AutoChat.getBooleanValue()) {
            String s = componentIn.toString();
            String[] arr = s.split(" ");

            if (arr.length >= 16) {
                if (arr[7].equals("literal{From") && arr[8].equals("}[style={color=light_purple}],") && (arr[16].contains("style={color=white}") || arr[16].contains("style={color=green}"))) {
                    if (client.player != null) {
                        if (PermissionHandler.hasPermission("cubeside.autochat")) {
                            client.player.networkHandler.sendChatCommand("r " + Configs.PermissionSettings.AutoChatAntwort.getStringValue());
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
                name.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff592").result().get()));
                MutableText accept = Text.literal("[Annehmen]");
                accept.setStyle(Style.EMPTY.withColor(TextColor.parse("#119e1d").result().get()).withClickEvent(new ClickEvent.RunCommand("/tpaccept")));
                MutableText deny = Text.literal(" [Ablehnen]");
                deny.setStyle(Style.EMPTY.withColor(TextColor.parse("#9e1139").result().get()).withClickEvent(new ClickEvent.RunCommand("/tpdeny")));

                if (args2[1].startsWith("fragt, ob er sich zu dir teleportieren darf.")) {
                    component.append(name);
                    MutableText message = Text.literal(" möchte sich zu dir teleportieren.\n");
                    message.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db").result().get()));
                    component.append(message);
                    component.append(accept);
                    component.append(deny);

                    if (Configs.Generic.TpaSound.getBooleanValue()) {
                        if (client.player != null) {
                            client.player.playSoundToPlayer(SoundEvent.of(Identifier.of("block.note_block.flute")), SoundCategory.PLAYERS, 20.0f, 0.5f);
                        }
                    }

                    componentIn = component;
                }

                if (args2[1].startsWith("fragt, ob du dich zu ihm teleportieren möchtest.")) {
                    component.append(name);
                    MutableText message = Text.literal(" möchte, dass du dich zu ihm teleportierst.\n");
                    message.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db").result().get()));
                    component.append(message);
                    component.append(accept);
                    component.append(deny);

                    if (Configs.Generic.TpaSound.getBooleanValue()) {
                        if (client.player != null) {
                            client.player.playSoundToPlayer(SoundEvent.of(Identifier.of("block.note_block.flute")), SoundCategory.PLAYERS, 20.0f, 0.5f);
                        }
                    }

                    componentIn = component;
                }

                if (args2[1].startsWith("hat deine Teleportierungsanfrage angenommen.")) {
                    component.append(name);
                    MutableText message = Text.literal(" hat deine Teleportierungsanfrage");
                    message.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db").result().get()));
                    component.append(message);
                    MutableText message2 = Text.literal(" angenommen.");
                    message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#119e1d").result().get()));
                    component.append(message2);
                    componentIn = component;
                }

                if (args2[1].startsWith("hat deine Teleportierungsanfrage abgelehnt.")) {
                    component.append(name);
                    MutableText message = Text.literal(" hat deine Teleportierungsanfrage");
                    message.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db").result().get()));
                    component.append(message);
                    MutableText message2 = Text.literal(" abgelehnt.");
                    message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#9e1139").result().get()));
                    component.append(message2);
                    componentIn = component;
                }
            }
            if (args5.length == 5) {
                MutableText name = Text.literal(args6[4].replace(".", ""));
                name.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff592").result().get()));

                if (tpamessage.startsWith("Du teleportierst dich zu")) {
                    MutableText message1 = Text.literal("Du wirst zu ");
                    message1.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db").result().get()));
                    component.append(message1);
                    component.append(name);
                    MutableText message2 = Text.literal(" teleportiert.");
                    message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db").result().get()));
                    component.append(message2);
                    componentIn = component;
                }
            }

            if (args6.length == 6) {
                MutableText name = Text.literal(args6[4].replace(".", ""));
                name.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff592").result().get()));
                if (tpamessage.startsWith("Eine Anfrage wurde an")) {
                    MutableText message1 = Text.literal("Du hast eine Anfrage an ");
                    message1.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db").result().get()));
                    component.append(message1);
                    component.append(name);
                    MutableText message2 = Text.literal(" gesendet.");
                    message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db").result().get()));
                    component.append(message2);
                    componentIn = component;
                }

                if (tpamessage.startsWith("Diese Anfrage wird nach")) {
                    MutableText message1 = Text.literal("Diese Anfrage wird in ");
                    message1.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db").result().get()));
                    component.append(message1);
                    component.append(name);
                    MutableText seconds = Text.literal(" Sekunden ");
                    seconds.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff592").result().get()));
                    component.append(seconds);
                    MutableText message2 = Text.literal("ablaufen.");
                    message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db").result().get()));
                    component.append(message2);
                    componentIn = component;
                }

            }
            if (tpamessage.equals("Teleportation läuft...")) {
                MutableText message = Text.literal("Teleportation läuft...");
                message.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db").result().get()));
                component.append(message);
                componentIn = component;
            }

            if (tpamessage.equals("Du hast die Teleportierungsanfrage abgelehnt.")) {
                MutableText message1 = Text.literal("Du hast die Teleportierungsanfrage");
                message1.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db").result().get()));
                component.append(message1);
                MutableText message2 = Text.literal(" abgelehnt.");
                message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#9e1139").result().get()));
                component.append(message2);
                componentIn = component;
            }

            if (tpamessage.equals("Du hast die Teleportierungsanfrage angenommen.")) {
                MutableText message1 = Text.literal("Du hast die Teleportierungsanfrage");
                message1.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db").result().get()));
                component.append(message1);
                MutableText message2 = Text.literal(" angenommen.");
                message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#119e1d").result().get()));
                component.append(message2);
                componentIn = component;
            }

            if (tpamessage.equals("Fehler: Du hast keine Teleportierungsanfragen.")) {
                MutableText message = Text.literal("Fehler: ");
                message.setStyle(Style.EMPTY.withColor(TextColor.parse("#9e1139").result().get()));
                component.append(message);
                MutableText message2 = Text.literal("Du hast keine Teleportierungsanfrage.");
                message2.setStyle(Style.EMPTY.withColor(TextColor.parse("#2ff5db").result().get()));
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
            addMessageToDatabase(component);
            componentIn = component;
        } else {
            addMessageToDatabase(componentIn);
        }

        if (Configs.Chat.CountDuplicateMessages.getBooleanValue()) {
            lastEditMessage = componentIn;
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
            ChatDatabase chatDatabase = CubesideClientFabric.getChatDatabase();
            if (chatDatabase != null) {
                try {
                    chatDatabase.addCommandEntry(message);
                } catch (Throwable e) {
                    CubesideClientFabric.LOGGER.log(Level.WARN, "Command can not save to Database " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void cubesideMod$addStoredChatMessage(Text message) {
        this.addVisibleMessage(new ChatHudLine(0, message, null, new MessageIndicator(10631423, null, Text.literal("*"), null)));
    }

    @Override
    public void cubesideMod$addStoredCommand(String message) {
        this.addToMessageHistory(message);
    }

    @Unique
    public void playAFKSound() {
        if (client.player != null) {
            SoundEvent sound = SoundEvent.of(Identifier.of(CubesideClientFabric.MODID, "afk"));
            client.player.playSoundToPlayer(sound, SoundCategory.PLAYERS, 0.2f, 1.0f);
        }
    }

    @Unique
    public void addMessageToDatabase(Text component) {
        if (Configs.Chat.SaveMessagesToDatabase.getBooleanValue()) {
            ChatDatabase chatDatabase = CubesideClientFabric.getChatDatabase();
            if (chatDatabase != null) {
                ClientWorld world = client.world;
                if (world != null) {
                    try {
                        RegistryOps<JsonElement> ops = world.getRegistryManager().getOps(JsonOps.INSTANCE);
                        chatDatabase.addMessageEntry(TextCodecs.CODEC.encode(component, ops, ops.empty()).getOrThrow().toString());
                    } catch (Throwable e) {
                        CubesideClientFabric.LOGGER.log(Level.WARN, "Message can not save to Database " + e.getMessage());
                    }
                }
            }
        }
    }
}
