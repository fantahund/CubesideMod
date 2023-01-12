package de.fanta.cubeside;

import com.mojang.blaze3d.platform.GlStateManager;
import de.fanta.cubeside.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class ChatInfoHud {
    private final MinecraftClient minecraft;
    private final TextRenderer fontRenderer;

    public ChatInfoHud() {
        this.minecraft = MinecraftClient.getInstance();
        this.fontRenderer = minecraft.textRenderer;
    }

    private static class RenderSize {
        int width;
        int height;

        RenderSize(int w, int h) {
            this.width = w;
            this.height = h;
        }
    }

    public void onRenderGameOverlayPost(MatrixStack stack) {
        if (minecraft.options.debugEnabled) {
            return;
        }
        if (this.minecraft.currentScreen instanceof ChatScreen && Configs.Chat.DisplayChatInfo.getBooleanValue()) {
            GlStateManager._clearColor(1.0f, 1.0f, 1.0f, 1.0f);
            renderChatInfoHud(stack);
        }
    }

    private void renderChatInfoHud(MatrixStack stack) {
        String currentChannelString = CubesideClientFabric.getCurrentGlobalChatPrivateMessagePlayer().equals("") ? CubesideClientFabric.getCurrentGlobalChatChannel().equals("") ? "" : "Aktueller Chat: " + (CubesideClientFabric.getCurrentGlobalChatChannel()) : "Aktueller Chat: " + CubesideClientFabric.getCurrentGlobalChatPrivateMessagePlayer();
        String currentResponsPlayerString = CubesideClientFabric.getCurrentGlobalChatPrivateMessageRespondPlayer().equals("") ? "" : "Antwort an: " + CubesideClientFabric.getCurrentGlobalChatPrivateMessageRespondPlayer();
        RenderSize result = new RenderSize(0, 0);
        int distance = 10;
        double e = this.minecraft.options.getTextBackgroundOpacity().getValue();
        int v = (int) (255.0 * 1.0 * e);
        int height = (int) (minecraft.getWindow().getScaledHeight() - (result.height + 70 / 2f));
        ChatHud.fill(stack, 2, height - 2, getWith(getWith(0, currentResponsPlayerString), currentChannelString) + 8, currentResponsPlayerString.equals("") ? height + 10 : height + 20, v << 24);

        result.width = getWith(result.width, currentChannelString);
        this.fontRenderer.drawWithShadow(stack, currentChannelString, 5, (minecraft.getWindow().getScaledHeight() - (result.height + 70 / 2f)), Color.WHITE.getRGB());
        result.height -= distance;
        this.fontRenderer.drawWithShadow(stack, currentResponsPlayerString, 5, (minecraft.getWindow().getScaledHeight() - (result.height + 70 / 2f)), Color.WHITE.getRGB());

        if (result.width != 0) {
            result.width += 20;
        }
    }

    private int getWith(int resultWidth, String text) {
        int width = this.fontRenderer.getWidth(text);
        return Math.max(width, resultWidth);
    }
}

