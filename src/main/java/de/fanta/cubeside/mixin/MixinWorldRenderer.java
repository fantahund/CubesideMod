package de.fanta.cubeside.mixin;

import de.fanta.cubeside.CubesideClientFabric;
import de.fanta.cubeside.config.Configs;
import de.fanta.cubeside.util.ColorUtils;
import fi.dy.masa.malilibcs.util.Color4f;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.awt.*;
import java.util.List;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {

    /**
     * @author fantahund
     * @reason Make BlockHitBoxFancy
     */
    @Overwrite
    private static void drawCuboidShapeOutline(MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape shape, double offsetX, double offsetY, double offsetZ, float red, float green, float blue, float alpha) {
        Color color;
        if (Configs.HitBox.RainbowBlockHitBox.getBooleanValue()) {
            List<Color4f> color4fList = Configs.HitBox.RainbowBlockHitBoxColorList.getColors();
            if (color4fList.isEmpty()) {
                color4fList = Configs.HitBox.RainbowBlockHitBoxColorList.getDefaultColors();
            }
            color = ColorUtils.getColorGradient(CubesideClientFabric.getTime(), Configs.HitBox.RainbowBlockHitBoxSpeed.getDoubleValue(), color4fList);
        } else {
            Color4f color4f = Configs.HitBox.BlockHitBoxColor.getColor();
            color = new Color(color4f.r, color4f.g, color4f.b);
        }
        red = color.getRed() / 255F;
        green = color.getGreen() / 255f;
        blue = color.getBlue() / 255f;
        alpha = (float) Configs.HitBox.BlockHitBoxVisibility.getDoubleValue();

        MatrixStack.Entry entry = matrices.peek();
        float finalRed = red;
        float finalGreen = green;
        float finalBlue = blue;
        float finalAlpha = alpha;
        shape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
            float k = (float) (maxX - minX);
            float l = (float) (maxY - minY);
            float m = (float) (maxZ - minZ);
            float n = MathHelper.sqrt(k * k + l * l + m * m);
            k /= n;
            l /= n;
            m /= n;
            vertexConsumer.vertex(entry.getPositionMatrix(), (float) (minX + offsetX), (float) (minY + offsetY), (float) (minZ + offsetZ)).color(finalRed, finalGreen, finalBlue, finalAlpha).normal(entry.getNormalMatrix(), k, l, m).next();
            vertexConsumer.vertex(entry.getPositionMatrix(), (float) (maxX + offsetX), (float) (maxY + offsetY), (float) (maxZ + offsetZ)).color(finalRed, finalGreen, finalBlue, finalAlpha).normal(entry.getNormalMatrix(), k, l, m).next();
        });
    }
}
