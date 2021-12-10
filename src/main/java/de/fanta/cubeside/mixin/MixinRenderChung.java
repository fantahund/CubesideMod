package de.fanta.cubeside.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.tick.QueryableTickScheduler;
import net.minecraft.world.tick.WorldTickScheduler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldTickScheduler.class)
public abstract class MixinRenderChung<T> implements QueryableTickScheduler<T> {

    @Inject(method = "visitChunks", at = @At("HEAD"))
    private void visitChinks(BlockBox box, WorldTickScheduler.ChunkVisitor<T> visitor, CallbackInfo ci) {
        ParticleType type = Particle
    }
}
