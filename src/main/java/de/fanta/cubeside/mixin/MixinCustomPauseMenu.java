package de.fanta.cubeside.mixin;

import de.fanta.cubeside.CubesideClientFabric;
import de.fanta.cubeside.config.ConfigGui;
import de.fanta.cubeside.data.SearchScreen;
import fi.dy.masa.malilib.gui.GuiBase;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.world.ClientWorld;
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
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("custombutton.cubeside.options"), button -> GuiBase.openGui(new ConfigGui())).dimensions(this.width / 2 - 100 + 205, this.height / 4 + 72 - 15, 100, 20).build());
        if (CubesideClientFabric.getChatDatabase() != null) {
            if (client == null) {
                return;
            }

            ClientWorld world = this.client.world;
            if (world != null) {
                this.addDrawableChild(ButtonWidget.builder(Text.literal("ChatLog (Beta)"), button -> client.setScreen(new SearchScreen(this, world.getRegistryManager()))).dimensions(this.width / 2 - 100 + 205, this.height / 4 + 72 - 16 + 25, 100, 20).build());
            }
        }
    }

}
