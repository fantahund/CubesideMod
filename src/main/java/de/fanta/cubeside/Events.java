package de.fanta.cubeside;

import de.fanta.cubeside.config.Configs;
import de.fanta.cubeside.util.ChatHudMethods;
import de.fanta.cubeside.util.ChatUtils;
import de.fanta.cubeside.util.SoundThread;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.option.Perspective;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Events {

    private boolean flyingLastTick = false;
    private Perspective lastmode = Perspective.FIRST_PERSON;
    private SoundEvent sound;

    private SoundThread soundThread;

    private boolean connect;

    private LocalDate lastTickDate;

    public Events() {
    }

    public void init() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (Configs.Chat.SaveMessagesToDatabase.getBooleanValue()) {
                if (client.getCurrentServerEntry() != null) {
                    String server = client.getCurrentServerEntry().address.toLowerCase();
                    if (CubesideClientFabric.databaseinuse) {
                        return;
                    }
                    List<Text> messages = CubesideClientFabric.getDatabase().loadMessages(server);
                    if (CubesideClientFabric.databaseinuse) {
                        return;
                    }
                    List<String> commands = CubesideClientFabric.getDatabase().loadCommands(server);
                    if (!connect) {
                        if (client.player != null) {
                            CubesideClientFabric.setLoadingMessages(true);
                            System.out.println("Messages: " + (long) messages.size());
                            messages.forEach(((ChatHudMethods) client.inGameHud.getChatHud())::addStoredChatMessage);
                            System.out.println("Commands: " + (long) commands.size());
                            commands.forEach(((ChatHudMethods) client.inGameHud.getChatHud())::addStoredCommand);
                            CubesideClientFabric.setLoadingMessages(false);
                            connect = true;
                            CubesideClientFabric.messageQueue.forEach(text -> client.inGameHud.getChatHud().addMessage(text));
                            CubesideClientFabric.messageQueue.clear();
                        }
                    }
                }
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (Configs.Chat.SaveMessagesToDatabase.getBooleanValue()) {
                connect = false;
                CubesideClientFabric.setRank(null);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player != null) {
                if (Configs.Generic.ThirdPersonElytra.getBooleanValue()) {
                    if (mc.player.isFallFlying()) {
                        if (!flyingLastTick) {
                            flyingLastTick = true;
                            lastmode = mc.options.getPerspective();
                            mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
                        }
                    } else {
                        if (flyingLastTick) {
                            flyingLastTick = false;
                            mc.options.setPerspective(lastmode);
                        }
                    }
                }

                if (Configs.Generic.ElytraAlarm.getBooleanValue()) {
                    if (sound == null) {
                        Identifier location;
                        location = new Identifier(CubesideClientFabric.MODID, "alarm");
                        sound = SoundEvent.of(location);
                    }
                    if (mc.player.isFallFlying() && mc.player.getY() <= mc.world.getBottomY()) {
                        if (soundThread == null) {
                            soundThread = SoundThread.of(1944, sound, mc.player);
                            soundThread.start();
                        }
                    } else if (soundThread != null && soundThread.isRunning()) {
                        soundThread.stopThread();
                        soundThread = null;
                    }
                }

                while (KeyBinds.AUTO_CHAT.wasPressed()) {
                    if (CubesideClientFabric.hasPermission("cubeside.autochat")) {
                        if (Configs.PermissionSettings.AutoChat.getBooleanValue()) {
                            Configs.PermissionSettings.AutoChat.setBooleanValue(false);
                            mc.player.sendMessage(Text.of("§cAuto Chat deaktiviert"), true);
                        } else {
                            Configs.PermissionSettings.AutoChat.setBooleanValue(true);
                            mc.player.sendMessage(Text.of("§aAuto Chat aktiviert"), true);
                        }
                        Configs.saveToFile();
                    } else {
                        ChatUtils.sendErrorMessage("AutoChat kannst du erst ab Staff benutzen!");
                    }
                }

                while (KeyBinds.TOGGLE_SHOW_ENTITIES_IN_SPECTATOR_MODE.wasPressed()) {
                    if (Configs.Generic.ShowInvisibleEntitiesInSpectator.getBooleanValue()) {
                        Configs.Generic.ShowInvisibleEntitiesInSpectator.setBooleanValue(false);
                        mc.player.sendMessage(Text.of("§aUnsichtbare Entities werden jetzt im Spectator nicht mehr angezeigt!"), true);
                    } else {
                        Configs.Generic.ShowInvisibleEntitiesInSpectator.setBooleanValue(true);
                        mc.player.sendMessage(Text.of("§aUnsichtbare Entities werden jetzt im Spectator wieder angezeigt!"), true);
                    }
                    Configs.saveToFile();
                }
            }

            if (Configs.Chat.SaveMessagesToDatabase.getBooleanValue()) {
                LocalDate date = LocalDate.now();
                if (lastTickDate != null) {
                    if (!lastTickDate.isEqual(date)) {
                        ChatUtils.sendNormalMessage("Neuer Tag: " + date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    }
                }
                lastTickDate = date;
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> CubesideClientFabric.commands.register(dispatcher));

    }
}
