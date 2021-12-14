package de.fanta.cubeside;

import de.fanta.cubeside.util.ChatHudMethods;
import de.fanta.cubeside.util.ChatUtils;
import de.fanta.cubeside.util.SoundThread;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.option.Perspective;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

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
            if (Config.saveMessagestoDatabase) {
                if (client.getCurrentServerEntry() != null) {
                    String server = client.getCurrentServerEntry().address.toLowerCase();
                    List<Text> messages = CubesideClient.getDatabase().loadMessages(server);
                    List<String> commands = CubesideClient.getDatabase().loadCommands(server);
                    if (!connect) {
                        if (client.player != null) {
                            CubesideClient cubeClient = CubesideClient.getInstance();
                            cubeClient.setLoadingMessages(true);
                            System.out.println("Messages: " + (long) messages.size());
                            messages.forEach(((ChatHudMethods) client.inGameHud.getChatHud())::addStoredChatMessage);
                            System.out.println("Commands: " + (long) commands.size());
                            commands.forEach(((ChatHudMethods) client.inGameHud.getChatHud())::addStoredCommand);
                            cubeClient.setLoadingMessages(false);
                            connect = true;
                            cubeClient.messageQueue.forEach(text -> client.inGameHud.getChatHud().addMessage(text));
                            cubeClient.messageQueue.clear();
                        }
                    }
                }
            }

        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (Config.saveMessagestoDatabase) {
                connect = false;
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player != null) {
                if (Config.thirdPersonElytra) {
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

                if (Config.elytraAlarm) {
                    if (sound == null) {
                        Identifier location;
                        location = new Identifier(CubesideClient.MODID, "alarm");
                        sound = new SoundEvent(location);
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

                while (CubesideClient.AUTO_CHAT.wasPressed()) {
                    if (CubesideClient.getInstance().hasPermission("cubeside.autochat")) {
                        if (Config.autochat) {
                            Config.autochat = false;
                            mc.player.sendMessage(Text.of("§cAuto Chat deaktiviert"), true);
                        } else {
                            Config.autochat = true;
                            mc.player.sendMessage(Text.of("§aAuto Chat aktiviert"), true);
                        }
                        Config.serialize();
                    } else {
                        ChatUtils.sendErrorMessage("AutoChat kannst du erst ab Staff benutzen!");
                    }
                }
                //GAMA
                while (CubesideClient.TOGGLE_GAMA.wasPressed()) {
                    double temp = mc.options.gamma;
                    mc.options.gamma = MathHelper.clamp(CubesideClient.prevGamma, CubesideClient.minGamma, CubesideClient.maxGamma);
                    mc.player.sendMessage(Text.of("§aGamma: §3" + mc.options.gamma), true);
                    CubesideClient.prevGamma = temp;
                }

                while (CubesideClient.TOGGLE_SHOW_ENTITIES_IN_SPECTATOR_MODE.wasPressed()) {
                    if (Config.showInvisibleEntitiesinSpectator) {
                        Config.showInvisibleEntitiesinSpectator = false;
                        mc.player.sendMessage(Text.of("§aUnsichtbare Entities werden jetzt im Spectator nicht mehr angezeigt!"), true);
                    } else {
                        Config.showInvisibleEntitiesinSpectator = true;
                        mc.player.sendMessage(Text.of("§aUnsichtbare Entities werden jetzt im Spectator wieder angezeigt!"), true);
                    }
                    Config.serialize();
                }
            }

            if (Config.saveMessagestoDatabase) {
                LocalDate date = LocalDate.now();
                if (lastTickDate != null) {
                    if (!lastTickDate.isEqual(date)) {
                        ChatUtils.sendNormalMessage("Neuer Tag: " + date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    }
                }
                lastTickDate = date;
            }
        });
    }

}
