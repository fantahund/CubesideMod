package de.fanta.cubeside.mixin;

import de.fanta.cubeside.Config;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.render.entity.EntityRenderer.class)
public class MixinWeihnachtsmarktLagFix<T extends Entity> {

    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (Config.weihnachtsmarkt) {
            if (entity instanceof ItemFrameEntity itemFrame) {
                ci.cancel();
                System.out.println("ItemFrame " + entity);
                /*if (itemFrame.getHeldItemStack().getItem() == Items.FILLED_MAP) {
                    ci.cancel();
                }*/
            }
            if (entity instanceof ArmorStandEntity armorStand) {
                ci.cancel();
                System.out.println("ArmorStand " + entity);
                /*if (armorStand.getEquippedStack(EquipmentSlot.HEAD).getItem() != Items.PLAYER_HEAD && armorStand.isInvisible()) {
                    ci.cancel();
                }*/
            }
        }
    }

}
