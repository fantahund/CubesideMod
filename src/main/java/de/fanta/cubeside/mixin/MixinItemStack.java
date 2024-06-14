package de.fanta.cubeside.mixin;

import de.fanta.cubeside.config.Configs;
import java.util.List;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemStack.class)
public class MixinItemStack {
    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/component/ComponentType;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V", ordinal = 4), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addMoreTooltip(Item.TooltipContext context, PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> ci, List<Text> list) {
        if (Configs.Generic.ShowAdditionalRepairCosts.getBooleanValue()) {
            ItemStack stack = (ItemStack) (Object) this;
            Integer repairCost = stack.getComponents().get(DataComponentTypes.REPAIR_COST);
            if (repairCost != null && repairCost > 0) {
                ItemEnchantmentsComponent enchantments = stack.getComponents().get(DataComponentTypes.ENCHANTMENTS);
                if (enchantments == null || enchantments.showInTooltip) {
                    list.add(Text.translatable("cubeside.additional_repair_costs").formatted(Formatting.RED).append(": " + repairCost));
                }
            }
        }
    }
}
