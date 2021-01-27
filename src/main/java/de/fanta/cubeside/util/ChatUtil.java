package de.fanta.cubeside.util;

import de.fanta.cubeside.Cubeside;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;

import java.util.HashMap;

public class ChatUtil {

    private final Thread timer;
    private static final HashMap<String, Object> messageQueue = new HashMap<>();

    public ChatUtil() {
        // prevent instances
        this.timer = new Thread(() -> {
            while (true) {
                synchronized (messageQueue) {
                    messageQueue.values().forEach(ChatUtil::sendMessage);
                    messageQueue.clear();
                }
                try {
                    Thread.sleep(3500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        this.timer.start();

    }

    public void stopTimer() {
        timer.stop();
    }

    public static void sendMessage(Object message) {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.inGameHud.addChatMessage(MessageType.CHAT, Text.of(Cubeside.PREFIX + message), mc.player.getUuid());
    }

    public static void sendNormalMessage(Object message) {
        sendMessage("§a" + message);
    }

    public static void sendWarningMessage(Object message) {
        sendMessage("§6" + message);
    }

    public static void sendErrorMessage(Object message) {
        sendMessage("§c" + message);
    }

    public static void queueMessage(Object message, String key) {
        synchronized (messageQueue) {
            messageQueue.putIfAbsent(key, "§a" + message);
        }
    }
}
