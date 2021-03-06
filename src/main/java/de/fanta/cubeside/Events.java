package de.fanta.cubeside;

import de.fanta.cubeside.util.SoundThread;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.options.Perspective;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Events {

    private boolean flyingLastTick = false;
    private Perspective lastmode = Perspective.FIRST_PERSON;
    private SoundEvent sound;

    private SoundThread soundThread;

    public Events() {
    }

    public void init() {
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
                    if (mc.player.isFallFlying() && mc.player.getY() <= 0) {
                        if (soundThread == null) {
                            soundThread = SoundThread.of(5, sound, mc.player);
                            soundThread.start();
                        }
                    } else if (soundThread != null && soundThread.isRunning()) {
                        soundThread.stopThread();
                        soundThread = null;
                    }
                }
            }
        });

        ClientTickCallback.EVENT.register(minecraftClient -> {
            while (CubesideClient.AUTO_CHAT.wasPressed()) {
                if (Config.autochat) {
                    Config.autochat = false;
                    minecraftClient.player.sendMessage(Text.of("§cAuto Chat deaktiviert"), true);
                } else {
                    Config.autochat = true;
                    minecraftClient.player.sendMessage(Text.of("§aAuto Chat aktiviert"), true);
                }
                Config.serialize();
            }
        });
    }


}
