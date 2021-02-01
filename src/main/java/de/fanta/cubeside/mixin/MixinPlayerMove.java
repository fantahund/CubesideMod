package de.fanta.cubeside.mixin;

import de.fanta.cubeside.Config;
import de.fanta.cubeside.CubesideClient;
import de.fanta.cubeside.util.SoundThread;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.Perspective;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerMove {

    MinecraftClient mc = MinecraftClient.getInstance();
    private boolean flyingLastTick = false;
    private Perspective lastmode = Perspective.FIRST_PERSON;
    private static SoundEvent sound;

    private SoundThread soundThread;

    @Inject(method = "travel", at = @At("HEAD"))
    public void travel(Vec3d movementInput, CallbackInfo info) {
        PlayerEntity player = mc.player;
        if (Config.thirdPersonElytra) {
            if (player.isFallFlying()) {
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
            if (this.soundThread == null) {
                this.soundThread = new SoundThread(5, sound, mc.player);
                this.soundThread.start();
            }
            if (player.isFallFlying() && player.getY() <= 100) {
                soundThread.resumeSounds();
            } else {
                soundThread.pauseSounds();
            }

        }
    }
}

