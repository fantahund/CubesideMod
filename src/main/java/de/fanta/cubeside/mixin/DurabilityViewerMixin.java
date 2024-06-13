package de.fanta.cubeside.mixin;

import de.guntram.mcmod.durabilityviewer.client.gui.GuiItemDurability;
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
                    stack.apply(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true, UnaryOperator.identity()); //TODO 1.21 ???

                }
            }
            return stack;
        }
        return itemStack;
    }
}
