package de.fanta.cubeside.mixin;

import de.fanta.cubeside.CubesideClientFabric;
import de.fanta.cubeside.config.Configs;
import de.fanta.cubeside.util.ColorUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.state.EntityHitbox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;
import java.util.List;

@Mixin(EnderDragonEntityRenderer.class)
public class MixinEnderDragonEntityRenderer {

    @Redirect(method = "appendHitboxes*", at = @At(value = "NEW", target = "(DDDDDDFFFFFF)Lnet/minecraft/client/render/entity/state/EntityHitbox;"))
    private EntityHitbox modifyEntityHitbox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float x, float y, float z, float r, float g, float b) {
        EntityHitbox entityHitbox;
        if (Configs.HitBox.RainbowEntityHitBox.getBooleanValue()) {
            List<Color4f> color4fList = Configs.HitBox.RainbowEntityHitBoxColorList.getColors();
            if (color4fList.isEmpty()) {
                color4fList = Configs.HitBox.RainbowEntityHitBoxColorList.getDefaultColors();
            }
            Color color = ColorUtils.getColorGradient(CubesideClientFabric.getTime(), Configs.HitBox.RainbowEntityHitBoxSpeed.getDoubleValue(), color4fList);
            entityHitbox = new EntityHitbox(minX, minY, minZ, maxX, maxY, maxZ, x, y, z, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
        } else {
            entityHitbox = new EntityHitbox(minX, minY, minZ, maxX, maxY, maxZ, x, y, z, r, g, b);
        }

        return entityHitbox;
    }

}
