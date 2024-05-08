package de.fanta.cubeside.mixin;

import de.fanta.cubeside.CubesideClientFabric;
import de.fanta.cubeside.config.Configs;
import de.fanta.cubeside.util.ColorUtils;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.List;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinCustomHitBox {

    /**
     * @author fantahund
     * @reason Make HitBoxes fancy
     */
    @Overwrite
    private static void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta) {
        Color color;
        if (Configs.HitBox.RainbowEntityHitBox.getBooleanValue()) {
            List<Color4f> color4fList = Configs.HitBox.RainbowEntityHitBoxColorList.getColors();
            if (color4fList.isEmpty()) {
                color4fList = Configs.HitBox.RainbowEntityHitBoxColorList.getDefaultColors();
            }
            color = ColorUtils.getColorGradient(CubesideClientFabric.getTime(), Configs.HitBox.RainbowEntityHitBoxSpeed.getDoubleValue(), color4fList);
        } else {
            Color4f color4f = Configs.HitBox.EntityHitBoxColor.getColor();
            color = new Color(color4f.r, color4f.g, color4f.b);
        }

        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        WorldRenderer.drawBox(matrices, vertices, box, color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, (float) Configs.HitBox.EntityHitBoxVisibility.getDoubleValue());

        if (entity instanceof EnderDragonEntity enderDragon) {
            double d = -MathHelper.lerp(tickDelta, enderDragon.lastRenderX, enderDragon.getX());
            double e = -MathHelper.lerp(tickDelta, enderDragon.lastRenderY, enderDragon.getY());
            double f = -MathHelper.lerp(tickDelta, enderDragon.lastRenderZ, enderDragon.getZ());
            EnderDragonPart[] enderDragonParts = enderDragon.getBodyParts();
            for (EnderDragonPart enderDragonPart : enderDragonParts) {
                matrices.push();
                double g = d + MathHelper.lerp(tickDelta, enderDragonPart.lastRenderX, enderDragonPart.getX());
                double h = e + MathHelper.lerp(tickDelta, enderDragonPart.lastRenderY, enderDragonPart.getY());
                double i = f + MathHelper.lerp(tickDelta, enderDragonPart.lastRenderZ, enderDragonPart.getZ());
                matrices.translate(g, h, i);
                if (!Configs.HitBox.RainbowEntityHitBox.getBooleanValue()) {
                    if (Configs.HitBox.EntityHitBoxColor.getColor().intValue == 16777215) {
                        color = new Color(64, 255, 0);
                    }
                }
                WorldRenderer.drawBox(matrices, vertices, enderDragonPart.getBoundingBox().offset(-enderDragonPart.getX(), -enderDragonPart.getY(), -enderDragonPart.getZ()), color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, (float) Configs.HitBox.EntityHitBoxVisibility.getDoubleValue());
                matrices.pop();
            }
        }

        if (Configs.HitBox.EntityHitBoxDirection.getBooleanValue()) {
            if (entity instanceof LivingEntity) {
                float j = 0.01F;
                WorldRenderer.drawBox(matrices, vertices, box.minX, (double)(entity.getStandingEyeHeight() - 0.01F), box.minZ, box.maxX, (double)(entity.getStandingEyeHeight() + 0.01F), box.maxZ, 1.0F, 0.0F, 0.0F, 1.0F);
            }

            Entity entity2 = entity.getVehicle();
            if (entity2 != null) {
                float k = Math.min(entity2.getWidth(), entity.getWidth()) / 2.0F;
                float l = 0.0625F;
                Vec3d vec3d = entity2.getPassengerRidingPos(entity).subtract(entity.getPos());
                WorldRenderer.drawBox(matrices, vertices, vec3d.x - (double)k, vec3d.y, vec3d.z - (double)k, vec3d.x + (double)k, vec3d.y + 0.0625, vec3d.z + (double)k, 1.0F, 1.0F, 0.0F, 1.0F);
            }

            Vec3d vec3d2 = entity.getRotationVec(tickDelta);
            MatrixStack.Entry entry = matrices.peek();
            vertices.vertex(entry, 0.0F, entity.getStandingEyeHeight(), 0.0F).color(0, 0, 255, 255).normal(entry, (float)vec3d2.x, (float)vec3d2.y, (float)vec3d2.z).next();
            vertices.vertex(entry, (float)(vec3d2.x * 2.0), (float)((double)entity.getStandingEyeHeight() + vec3d2.y * 2.0), (float)(vec3d2.z * 2.0)).color(0, 0, 255, 255).normal(entry, (float)vec3d2.x, (float)vec3d2.y, (float)vec3d2.z).next();
        }
    }

    @Inject(at = @At("RETURN"), method = "setRenderHitboxes")
    public void setRenderHitboxes(boolean renderHitboxes, CallbackInfo ci) {
        Configs.HitBox.ShowHitBox.setBooleanValue(renderHitboxes);
        Configs.saveToFile();
    }
}
