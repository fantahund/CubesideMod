package de.fanta.cubeside.mixin;

import de.fanta.cubeside.Config;
import net.minecraft.client.render.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.render.entity.EntityRenderer.class)
public class MixinWeihnachtsmarktLagFix<T extends Entity> {

    @Inject(at = @At("HEAD"), method = "shouldRender")
    public void render(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (!Config.weihnachtsmarkt) {
            return;
        }
        if (entity instanceof ItemFrameEntity itemFrame) {
            if (itemFrame.getHeldItemStack().getItem() == Items.FILLED_MAP) {
                cir.setReturnValue(false);
            }
        }
        if (entity instanceof ArmorStandEntity armorStand) {
            if (armorStand.getEquippedStack(EquipmentSlot.HEAD).getItem() != Items.PLAYER_HEAD && armorStand.isInvisible()) {
                cir.setReturnValue(false);
            }
        }
    }

}
