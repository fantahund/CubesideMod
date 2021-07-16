package de.fanta.cubeside;

import de.fanta.cubeside.util.ChatUtils;
import de.fanta.cubeside.util.SoundThread;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.option.Perspective;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

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
            }
        });

        ClientTickCallback.EVENT.register(minecraftClient -> {
            while (CubesideClient.AUTO_CHAT.wasPressed()) {
                if (CubesideClient.getInstance().hasPermission("cubeside.autochat")) {
                    if (Config.autochat) {
                        Config.autochat = false;
                        minecraftClient.player.sendMessage(Text.of("§cAuto Chat deaktiviert"), true);
                    } else {
                        Config.autochat = true;
                        minecraftClient.player.sendMessage(Text.of("§aAuto Chat aktiviert"), true);
                    }
                    Config.serialize();
                } else {
                    ChatUtils.sendErrorMessage("AutoChat kannst du erst ab Staff benutzen!");
                }
            }
            //GAMA
            while (CubesideClient.TOGGLE_GAMA.wasPressed()) {
                double temp = minecraftClient.options.gamma;
                minecraftClient.options.gamma = MathHelper.clamp(CubesideClient.prevGamma, CubesideClient.minGamma, CubesideClient.maxGamma);
                minecraftClient.player.sendMessage(Text.of("§aGamma: §3" + minecraftClient.options.gamma), true);
                CubesideClient.prevGamma = temp;
            }
        });
    }


}
