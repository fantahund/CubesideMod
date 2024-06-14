package de.fanta.cubeside.mixin;

import de.fanta.cubeside.util.ItemUtils;
import de.iani.cubesideutils.Pair;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class MixinItemRenderer {

    @Shadow
    public abstract void fill(RenderLayer layer, int x1, int y1, int x2, int y2, int color);

    @Shadow
    @Final
    private MatrixStack matrices;

    @Inject(method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("RETURN"))
    private void renderGuiItemOverlay(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {
        if (stack.isEmpty()) {
            return;
        }
        LoreComponent loreComponent = stack.get(DataComponentTypes.LORE);
        if (loreComponent != null) {
            Pair<Integer, Integer> damageValues = ItemUtils.getDamageValuesFormCustomItem(loreComponent);
            int damage = damageValues.first;
            int maxDamage = damageValues.second;
            if (damage == -1 || maxDamage == -1 || damage == maxDamage) {
                return;
            }

            int i = getItemBarStep(damage, maxDamage);
            int j = getItemBarColor(damage, maxDamage);
            int k = x + 2;
            int l = y + 13;
            this.matrices.push();
            this.fill(RenderLayer.getGuiOverlay(), k, l, k + 13, l + 2, -16777216);
            this.fill(RenderLayer.getGuiOverlay(), k, l, k + i, l + 1, j | -16777216);
            this.matrices.pop();
        }
    }

    @Unique
    private int getItemBarColor(int damage, int maxDamage) {
        float f = Math.max(0.0F, ((float) maxDamage - (float) (maxDamage - damage)) / (float) maxDamage);
        return MathHelper.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Unique
    private int getItemBarStep(int damage, int maxDamage) {
        return MathHelper.clamp(Math.round(13.0F - (float) (maxDamage - damage) * 13.0F / (float) maxDamage), 0, 13);
    }
}
