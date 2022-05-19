package de.fanta.cubeside.mixin;

import de.fanta.cubeside.KeyBinds;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Keyboard.class)
public class MixinKeyboard {

    @ModifyConstant(method = "onKey",  constant = @Constant(intValue = 66))
    private int narratorkey(int old) {
        return KeyBindingHelper.getBoundKeyOf(KeyBinds.NARRATOR_KEYBINDING).getCode();
    }
}
