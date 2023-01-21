package de.fanta.cubeside.event;

import de.fanta.cubeside.CubesideClientFabric;
import de.fanta.cubeside.util.ChatInfo;
import de.fanta.cubeside.util.FlashColorScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;

public class CubesideModChannelHandler implements ClientPlayNetworking.PlayChannelHandler {

    public static final Identifier CHANNEL_IDENTIFIER = new Identifier("cubesidemod", "data");

    public CubesideModChannelHandler() {
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL_IDENTIFIER, this);
    }

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf packet, PacketSender sender) {
        int globalChatDataChannelID = 0;
        int challengeFlashScreenDataChannelID = 1;
        try {
            int cubesideDateChannel = packet.readByte();
            int cubesideDateChannelVersion = packet.readByte();
            if (cubesideDateChannel == globalChatDataChannelID && cubesideDateChannelVersion == 0) {
                String currentChannelName = packet.readString();
                String currentPrivateChat = packet.readString();
                String currentResponsePartner = packet.readString();
                MutableText currentChannelColor = Text.empty();
                MutableText currentPrivateChatPrefix = Text.empty();
                MutableText currentResponsePartnerPrefix = Text.empty();
                try {
                    currentChannelColor = (MutableText) packet.readText();
                    currentPrivateChatPrefix = (MutableText) packet.readText();
                    currentResponsePartnerPrefix = (MutableText) packet.readText();
                } catch (IndexOutOfBoundsException ignored) {
                }

                ChatInfo chatInfo = new ChatInfo(currentChannelName, currentPrivateChat, currentResponsePartner, currentChannelColor, currentPrivateChatPrefix, currentResponsePartnerPrefix);
                CubesideClientFabric.setChatInfo(chatInfo);
            }

            if (cubesideDateChannel == challengeFlashScreenDataChannelID && cubesideDateChannelVersion == 0) {
                int color = packet.readInt();
                int duration = packet.readInt();
                FlashColorScreen.flashColoredScreen(duration, new Color(color));
            }
        } catch (Exception e) {
            CubesideClientFabric.LOGGER.warn("Unable to read CubesideMod data", e);
        }
    }
}

