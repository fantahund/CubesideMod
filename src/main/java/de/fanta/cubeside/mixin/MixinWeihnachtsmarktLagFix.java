package de.fanta.cubeside.mixin;

import de.fanta.cubeside.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
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

@Mixin(WorldRenderer.class)
public class MixinWeihnachtsmarktLagFix {

    @Inject(at = @At("HEAD"), method = "renderEntity", cancellable = true)
    public void render(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        if (Config.weihnachtsmarkt) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                if (entity instanceof ItemFrameEntity itemFrame) {
                    if (itemFrame.getHeldItemStack().getItem() == Items.FILLED_MAP) {
                        ci.cancel();
                    }
                }
                if (entity instanceof ArmorStandEntity armorStand) {
                    if (armorStand.getEquippedStack(EquipmentSlot.HEAD).getItem() != Items.PLAYER_HEAD) {
                        ci.cancel();
                    }
                }
            }
        }
    }

}
