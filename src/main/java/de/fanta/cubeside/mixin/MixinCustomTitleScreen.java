package de.fanta.cubeside.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.UnknownHostException;

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
        /*MultiplayerServerListPinger serverListPinger = new MultiplayerServerListPinger();
        try {
            serverListPinger.add(selectedEntry, () -> {});
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        serverListPinger.tick();*/
    }

    @Inject(at = @At("TAIL"), method = "initWidgetsNormal")
    private void addCustomButton(int y, int spacingY, CallbackInfo ci) {
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("custombutton.cubeside.joincubeside"), button -> {
            if (selectedEntry != null) {
                ConnectScreen.connect(this, MinecraftClient.getInstance(), new ServerAddress("cubeside.de", 25565), selectedEntry, false, null);
            }
        }).dimensions(this.width / 2 - 100 + 205, y + 14, 80, 20).build());
    }

}
