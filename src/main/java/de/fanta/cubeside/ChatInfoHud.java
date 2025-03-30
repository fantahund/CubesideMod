package de.fanta.cubeside;

import de.fanta.cubeside.config.Configs;
import de.fanta.cubeside.util.ChatInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
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

    public void onRenderChatInfoHud(DrawContext context) {
        if (Configs.Chat.DisplayChatInfo.getBooleanValue() && CubesideClientFabric.getChatInfo() != null) {
            renderChatInfoHud(context, CubesideClientFabric.getChatInfo());
        }
    }

    private void renderChatInfoHud(DrawContext context, ChatInfo chatInfo) {
        MutableText currentChannelText = Text.literal("Aktueller Chat: ");
        MutableText currentChannelColorText = chatInfo.isPrivatChat() ? chatInfo.getColoredPrivatText() : chatInfo.getColoredChannelText();
        currentChannelText.append(currentChannelColorText);

        MutableText currentResponseText = Text.literal(chatInfo.hasResponsePlayer() ? "Antwort an: " : "");
        MutableText currentResponseColorText = chatInfo.hasResponsePlayer() ? chatInfo.getColoredResponseText() : Text.empty();
        currentResponseText.append(currentResponseColorText);

        RenderSize result = new RenderSize(0, 0);
        int distance = 10;
        int height = (int) (minecraft.getWindow().getScaledHeight() - (result.height + 70 / 2f));

        context.fill(2, height - 2, getWith(getWith(0, currentResponseText.getString()), currentChannelText.getString()) + 8, !chatInfo.hasResponsePlayer() ? height + 10 : height + 20, minecraft.options.getTextBackgroundColor(Integer.MIN_VALUE));

        result.width = getWith(result.width, currentChannelText.getString());
        context.drawText(this.fontRenderer, currentChannelText, 5, (minecraft.getWindow().getScaledHeight() - (result.height + 70 / 2)), Color.WHITE.getRGB(), true);
        result.height -= distance;
        context.drawText(this.fontRenderer, currentResponseText, 5, (minecraft.getWindow().getScaledHeight() - (result.height + 70 / 2)), Color.WHITE.getRGB(), true);

        if (result.width != 0) {
            result.width += 20;
        }
    }

    private int getWith(int resultWidth, String text) {
        int width = this.fontRenderer.getWidth(text);
        return Math.max(width, resultWidth);
    }
}