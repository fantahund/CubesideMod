package de.fanta.cubeside.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class MixinCustomTitleScreen extends Screen {

    protected MixinCustomTitleScreen(Text title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "initWidgetsNormal")
    private void addCustomButton(int y, int spacingY, CallbackInfo ci) {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100 + 205, y + 14 , 80, 20, Text.translatable("custombutton.cubeside.joincubeside"), (ButtonWidget) -> {
            ServerInfo selectedEntry = new ServerInfo("Cubeside", "Cubeside.de", false);
            ConnectScreen.connect(this, MinecraftClient.getInstance(), new ServerAddress("cubeside.de", 25565), selectedEntry);
        }));
    }

}
