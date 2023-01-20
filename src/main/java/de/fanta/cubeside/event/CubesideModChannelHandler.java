package de.fanta.cubeside.event;

import de.fanta.cubeside.CubesideClientFabric;
import de.fanta.cubeside.util.ChatInfo;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CubesideModChannelHandler implements ClientPlayNetworking.PlayChannelHandler {

    public static final Identifier CHANNEL_IDENTIFIER = new Identifier("cubesidemod", "data");

    public CubesideModChannelHandler() {
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL_IDENTIFIER, this);
    }

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf packet, PacketSender sender) {
        try {
            int cubesideDateChannel = packet.readByte();
            int cubesideDateChannelVersion = packet.readByte();
            if (cubesideDateChannel == 0 && cubesideDateChannelVersion == 0) {
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
        } catch (Exception e) {
            CubesideClientFabric.LOGGER.warn("Unable to read CubesideMod data", e);
        }
    }
}
