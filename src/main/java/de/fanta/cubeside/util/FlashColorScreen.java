package de.fanta.cubeside.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class FlashColorScreen {
    private static boolean isRunning = false;
    private static int duration;
    private static Color color;
    private static int counter = 0;

    public static void flashColoredScreen(int durationInTicks, Color newColor) {
        if (!isRunning) {
            isRunning = true;
            duration = durationInTicks;
            color = newColor;
        }
    }

    public static void onClientTick(DrawContext drawContext) {
        if (isRunning && counter < duration) {
            MinecraftClient mc = MinecraftClient.getInstance();
            int width = mc.getWindow().getScaledWidth();
            int height = mc.getWindow().getScaledHeight();
            Color newColor;
            if (counter < duration / 2) {
                newColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (100 * (counter / (duration / 2f))));
            } else {
                newColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (100 * (2 - (counter / (duration / 2f)))));
            }
            drawContext.fill(0, 0, width, height, newColor.getRGB());
            counter++;
        } else {
            isRunning = false;
            counter = 0;
        }
    }
}
