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
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.awt.*;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinCustomHitBox {

    /**
     * @author fantahund
     * @reason Make HitBoxes fancy
     */
    @Overwrite
    private static void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta) {
        Color color;
        if (Configs.Fun.RainbowHitBox.getBooleanValue()) {
            if (Configs.Fun.RainbowHitBoxPastel.getBooleanValue()) {
                int[] baseColors = new int[] { 0xff7575, 0xff9e75, 0xffdf75, 0x9eff75, 0x7598ff, 0xb875ff }; //TODO Read from Config Color List. (Wait for MaLiLib update)
                color = ColorUtils.getColorGradient(CubesideClientFabric.getTime(), Configs.Fun.RainbowHitBoxSpeed.getDoubleValue(), baseColors);
            } else {
                color = ColorUtils.getColor(CubesideClientFabric.getTime(), Configs.Fun.RainbowHitBoxSpeed.getDoubleValue());
            }
        } else {
            Color4f color4f = Configs.Fun.HitBoxColor.getColor();
            color = new Color(color4f.r, color4f.g, color4f.b);
        }

        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        WorldRenderer.drawBox(matrices, vertices, box, color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.2F);

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
                if (!Configs.Fun.RainbowHitBox.getBooleanValue()) {
                    if (Configs.Fun.HitBoxColor.getColor().intValue == 16777215) {
                        color = new Color(64, 255, 0);
                    }
                }
                WorldRenderer.drawBox(matrices, vertices, enderDragonPart.getBoundingBox().offset(-enderDragonPart.getX(), -enderDragonPart.getY(), -enderDragonPart.getZ()), color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1.0F);
                matrices.pop();
            }
        }

        if (Configs.Fun.HitBoxDirection.getBooleanValue()) {
            if (entity instanceof LivingEntity) {
                WorldRenderer.drawBox(matrices, vertices, box.minX, entity.getStandingEyeHeight() - 0.01F, box.minZ, box.maxX, entity.getStandingEyeHeight() + 0.01F, box.maxZ, 1.0F, 0.0F, 0.0F, 1.0F);
            }

            Vec3d vec3d = entity.getRotationVec(tickDelta);
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            Matrix3f matrix3f = matrices.peek().getNormalMatrix();
            vertices.vertex(matrix4f, 0.0F, entity.getStandingEyeHeight(), 0.0F).color(0, 0, 255, 255).normal(matrix3f, (float) vec3d.x, (float) vec3d.y, (float) vec3d.z).next();
            vertices.vertex(matrix4f, (float) (vec3d.x * 2.0), (float) ((double) entity.getStandingEyeHeight() + vec3d.y * 2.0), (float) (vec3d.z * 2.0)).color(0, 0, 255, 255).normal(matrix3f, (float) vec3d.x, (float) vec3d.y, (float) vec3d.z).next();
        }
    }
}
