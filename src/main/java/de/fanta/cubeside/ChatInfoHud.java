package de.fanta.cubeside;

import com.mojang.blaze3d.platform.GlStateManager;
import de.fanta.cubeside.config.Configs;
import de.fanta.cubeside.util.ChatInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

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
        if (this.minecraft.currentScreen instanceof ChatScreen && Configs.Chat.DisplayChatInfo.getBooleanValue() && CubesideClientFabric.getChatInfo() != null) {
            GlStateManager._clearColor(1.0f, 1.0f, 1.0f, 1.0f);
            renderChatInfoHud(stack, CubesideClientFabric.getChatInfo());
        }
    }

    private void renderChatInfoHud(MatrixStack stack, ChatInfo chatInfo) {
        MutableText currentChannelText = Text.literal("Aktueller Chat: ");
        MutableText currentChannelColorText = chatInfo.isPrivatChat() ? chatInfo.getColoredPrivatText() : chatInfo.getColoredChannelText();
        currentChannelText.append(currentChannelColorText);

        MutableText currentResponseText = Text.literal(chatInfo.hasResponsePlayer() ? "Antwort an: " : "" );
        MutableText currentResponseColorText = chatInfo.hasResponsePlayer() ? chatInfo.getColoredResponseText() : Text.empty();
        currentResponseText.append(currentResponseColorText);

        RenderSize result = new RenderSize(0, 0);
        int distance = 10;
        double e = this.minecraft.options.getTextBackgroundOpacity().getValue();
        int v = (int) (255.0 * 1.0 * e);
        int height = (int) (minecraft.getWindow().getScaledHeight() - (result.height + 70 / 2f));
        ChatHud.fill(stack, 2, height - 2, getWith(getWith(0, currentResponseText.getString()), currentChannelText.getString()) + 8, !chatInfo.hasResponsePlayer() ? height + 10 : height + 20, v << 24);

        result.width = getWith(result.width, currentChannelText.getString());
        this.fontRenderer.drawWithShadow(stack, currentChannelText, 5, (minecraft.getWindow().getScaledHeight() - (result.height + 70 / 2f)), Color.WHITE.getRGB());
        result.height -= distance;
        this.fontRenderer.drawWithShadow(stack, currentResponseText, 5, (minecraft.getWindow().getScaledHeight() - (result.height + 70 / 2f)), Color.WHITE.getRGB());

        if (result.width != 0) {
            result.width += 20;
        }
    }

    private int getWith(int resultWidth, String text) {
        int width = this.fontRenderer.getWidth(text);
        return Math.max(width, resultWidth);
    }
}