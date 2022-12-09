package de.fanta.cubeside.mixin;

import de.fanta.cubeside.config.ConfigGui;
import fi.dy.masa.malilibcs.gui.GuiBase;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class MixinCustomPauseMenu extends Screen {

    protected MixinCustomPauseMenu(Text title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "initWidgets")
    private void addCustomButton(CallbackInfo ci) {
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("custombutton.cubeside.options"), button -> GuiBase.openGui(new ConfigGui())).dimensions(this.width / 2 - 100 + 205, this.height / 4 + 72 - 16, 100, 20).build());
    }

}
