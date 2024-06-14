package de.fanta.cubeside.mixin;

import de.fanta.cubeside.util.ItemUtils;
import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
import de.iani.cubesideutils.Pair;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
            Pair<Integer, Integer> damageValues = ItemUtils.getDamageValuesFormCustomItem(loreComponent);
            int durablility = damageValues.first;
            int maxDurablility = damageValues.second;


            if (durablility == -1 || maxDurablility == -1) {
                return itemStack;
            }

            ItemStack stack = null;

            if (slot == EquipmentSlot.HEAD) {
                if (maxDurablility == 55) {
                    stack = new ItemStack(Items.LEATHER_HELMET);
                } else if (maxDurablility == 77) {
                    stack = new ItemStack(Items.GOLDEN_HELMET);
                } else if (maxDurablility == 165) {
                    stack = new ItemStack(Items.IRON_HELMET);
                } else if (maxDurablility == 363) {
                    stack = new ItemStack(Items.DIAMOND_HELMET);
                } else if (maxDurablility == 407) {
                    stack = new ItemStack(Items.NETHERITE_HELMET);
                } else if (maxDurablility == 275) {
                    stack = new ItemStack(Items.TURTLE_HELMET);
                }
            } else if (slot == EquipmentSlot.CHEST) {
                if (maxDurablility == 80) {
                    stack = new ItemStack(Items.LEATHER_CHESTPLATE);
                } else if (maxDurablility == 112) {
                    stack = new ItemStack(Items.GOLDEN_CHESTPLATE);
                } else if (maxDurablility == 240) {
                    stack = new ItemStack(Items.IRON_CHESTPLATE);
                } else if (maxDurablility == 528) {
                    stack = new ItemStack(Items.DIAMOND_CHESTPLATE);
                } else if (maxDurablility == 592) {
                    stack = new ItemStack(Items.NETHERITE_CHESTPLATE);
                }
            } else if (slot == EquipmentSlot.LEGS) {
                if (maxDurablility == 75) {
                    stack = new ItemStack(Items.LEATHER_LEGGINGS);
                } else if (maxDurablility == 105) {
                    stack = new ItemStack(Items.GOLDEN_LEGGINGS);
                } else if (maxDurablility == 225) {
                    stack = new ItemStack(Items.IRON_LEGGINGS);
                } else if (maxDurablility == 495) {
                    stack = new ItemStack(Items.DIAMOND_LEGGINGS);
                } else if (maxDurablility == 555) {
                    stack = new ItemStack(Items.NETHERITE_LEGGINGS);
                }
            } else if (slot == EquipmentSlot.FEET) {
                if (maxDurablility == 65) {
                    stack = new ItemStack(Items.LEATHER_BOOTS);
                } else if (maxDurablility == 91) {
                    stack = new ItemStack(Items.GOLDEN_BOOTS);
                } else if (maxDurablility == 195) {
                    stack = new ItemStack(Items.IRON_BOOTS);
                } else if (maxDurablility == 429) {
                    stack = new ItemStack(Items.DIAMOND_BOOTS);
                } else if (maxDurablility == 481) {
                    stack = new ItemStack(Items.NETHERITE_BOOTS);
                }
            }

            if (stack != null) {
                stack.setDamage(maxDurablility - durablility);
                if (itemStack.hasEnchantments()) {
                    stack.addEnchantment(Enchantments.UNBREAKING, 1);
                }
            }
            return stack;
        }
        return itemStack;
    }
}
