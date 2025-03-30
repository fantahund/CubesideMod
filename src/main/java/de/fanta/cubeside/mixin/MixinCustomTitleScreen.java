package de.fanta.cubeside.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TitleScreen.class)
public abstract class MixinCustomTitleScreen extends Screen {

    protected MixinCustomTitleScreen(Text title) {
        super(title);
    }

    @Unique
    private ServerInfo selectedEntry;

    @Inject(at = @At("TAIL"), method = "init")
    private void init(CallbackInfo ci) {
        selectedEntry = new ServerInfo("Cubeside", "Cubeside.de", ServerInfo.ServerType.OTHER);
    }

    @Inject(at = @At("HEAD"), method = "addNormalWidgets")
    private void addCustomButton(int y, int spacingY, CallbackInfoReturnable<Integer> cir) {
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("custombutton.cubeside.joincubeside"), button -> {
            if (selectedEntry != null) {
                ConnectScreen.connect(this, MinecraftClient.getInstance(), new ServerAddress("cubeside.de", 25565), selectedEntry, false, null);
            }
        }).dimensions(this.width / 2 - 100 + 205, y + spacingY, 80, 20).build());
    }

}
