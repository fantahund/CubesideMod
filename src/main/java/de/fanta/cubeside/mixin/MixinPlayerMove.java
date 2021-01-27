package de.fanta.cubeside.mixin;

import de.fanta.cubeside.Config;
import de.fanta.cubeside.util.ChatUtil;
import net.fabricmc.fabric.mixin.resource.loader.MixinKeyedResourceReloadListener;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.Perspective;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.text.DecimalFormat;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerMove {
    MinecraftClient mc = MinecraftClient.getInstance();
    private boolean flyingLastTick = false;
    private Perspective lastmode = Perspective.FIRST_PERSON;

    @Inject(method = "travel", at = @At("HEAD"))
    public void travel(Vec3d movementInput, CallbackInfo info) {
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

    }
}

