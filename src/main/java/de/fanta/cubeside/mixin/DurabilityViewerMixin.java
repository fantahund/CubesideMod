package de.fanta.cubeside.mixin;

import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.UnaryOperator;

@Mixin(GuiItemDurability.class)
public class DurabilityViewerMixin {

    @Redirect(method = "onRenderGameOverlayPost", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    public ItemStack onRenderGameOverlayPost(PlayerEntity instance, EquipmentSlot slot) {
        ItemStack itemStack = instance.getEquippedStack(slot);
        if (slot == EquipmentSlot.HEAD || slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET) {
            if (itemStack.getItem() == Items.POLISHED_BLACKSTONE_BUTTON) {
                itemStack = getItemStackFromNBT(itemStack, slot);
            }
        }
        return itemStack;
    }

    @Unique
    private static ItemStack getItemStackFromNBT(ItemStack itemStack, EquipmentSlot slot) {
        LoreComponent loreComponent = itemStack.get(DataComponentTypes.LORE);
        if (loreComponent != null) {
            int durablility = -1;
            int maxDurablility = -1;

            Text mutableText = loreComponent.lines().getLast();
            if (mutableText != null) {
                String fullDurabilityString = mutableText.getString();
                if (fullDurabilityString.startsWith("Haltbarkeit:")) {
                    String[] splitFull = fullDurabilityString.split(" ", 2);
                    if (splitFull.length == 2) {
                        String durabilityString = Text.literal(splitFull[1]).getString();
                        String[] splitDurability = durabilityString.split("/", 2);
                        if (splitDurability.length == 2) {
                            try {
                                durablility = Integer.parseInt(splitDurability[0]);
                                maxDurablility = Integer.parseInt(splitDurability[1]);
                            } catch (NumberFormatException ignore) {
                            }
                        }
                    }
                }
            }

            if (durablility == -1 || maxDurablility == -1) {
                return itemStack;
            }

            ItemStack stack = null;
            switch (slot) {
                case EquipmentSlot.HEAD -> {
                    switch (maxDurablility) {
                        case 55 -> stack = new ItemStack(Items.LEATHER_HELMET);
                        case 77 -> stack = new ItemStack(Items.GOLDEN_HELMET);
                        case 165 -> stack = new ItemStack(Items.IRON_HELMET);
                        case 363 -> stack = new ItemStack(Items.DIAMOND_HELMET);
                        case 407 -> stack = new ItemStack(Items.NETHERITE_HELMET);
                        case 275 -> stack = new ItemStack(Items.TURTLE_HELMET);
                    }
                }
                case EquipmentSlot.CHEST -> {
                    switch (maxDurablility) {
                        case 80 -> stack = new ItemStack(Items.LEATHER_CHESTPLATE);
                        case 112 -> stack = new ItemStack(Items.GOLDEN_CHESTPLATE);
                        case 240 -> stack = new ItemStack(Items.IRON_CHESTPLATE);
                        case 528 -> stack = new ItemStack(Items.DIAMOND_CHESTPLATE);
                        case 592 -> stack = new ItemStack(Items.NETHERITE_CHESTPLATE);
                    }
                }
                case EquipmentSlot.LEGS -> {
                    switch (maxDurablility) {
                        case 75 -> stack = new ItemStack(Items.LEATHER_LEGGINGS);
                        case 105 -> stack = new ItemStack(Items.GOLDEN_LEGGINGS);
                        case 225 -> stack = new ItemStack(Items.IRON_LEGGINGS);
                        case 495 -> stack = new ItemStack(Items.DIAMOND_LEGGINGS);
                        case 555 -> stack = new ItemStack(Items.NETHERITE_LEGGINGS);
                    }
                }
                case EquipmentSlot.FEET -> {
                    switch (maxDurablility) {
                        case 65 -> stack = new ItemStack(Items.LEATHER_BOOTS);
                        case 91 -> stack = new ItemStack(Items.GOLDEN_BOOTS);
                        case 195 -> stack = new ItemStack(Items.IRON_BOOTS);
                        case 429 -> stack = new ItemStack(Items.DIAMOND_BOOTS);
                        case 481 -> stack = new ItemStack(Items.NETHERITE_BOOTS);
                    }
                }
            }

            if (stack == null) {
                return itemStack;
            }

            stack.setDamage(maxDurablility - durablility);
            if (itemStack.hasEnchantments()) {
                stack.apply(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true, UnaryOperator.identity()); //TODO 1.21 ???

            }

            return stack;
        }
        return itemStack;
    }
}
