package de.fanta.cubeside.mixin;

import de.fanta.cubeside.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class MixinWeihnachtsmarktLagFix {

    @Inject(at = @At("HEAD"), method = "shouldRender", cancellable = true)
    public void render(Entity entity, Frustum frustum, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> cir) {
        if (Config.weihnachtsmarkt) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                if (entity instanceof ItemFrameEntity itemFrame) {
                    if (itemFrame.getHeldItemStack().getItem() == Items.FILLED_MAP) {
                        cir.setReturnValue(false);

                    }
                }
                if (entity instanceof ArmorStandEntity armorStand) {
                    if (armorStand.getEquippedStack(EquipmentSlot.HEAD).getItem() != Items.PLAYER_HEAD) {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }

}
