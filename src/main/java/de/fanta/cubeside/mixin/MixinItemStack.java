package de.fanta.cubeside.mixin;

import java.util.function.Consumer;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.MergedComponentMap;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class MixinItemStack {
    @Shadow
    MergedComponentMap components;

    @Inject(method = "appendComponentTooltip", at = @At(value = "RETURN"))
    private <T extends TooltipAppender> void appendComponentTooltip(ComponentType<T> componentType, Item.TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type, CallbackInfo ci) {
        if (componentType == DataComponentTypes.ENCHANTMENTS && displayComponent.shouldDisplay(componentType)) {
            Integer repairCost = components.get(DataComponentTypes.REPAIR_COST);
            if (repairCost != null && repairCost > 0) {
                textConsumer.accept(Text.translatable("cubeside.additional_repair_costs").formatted(Formatting.RED).append(": " + repairCost));
            }
        }
    }
}
