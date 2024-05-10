package de.fanta.cubeside.packets;

import de.fanta.cubeside.CubesideClientFabric;
import de.fanta.cubeside.util.ChatInfo;
import java.awt.Color;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CubesideDataS2C implements CustomPayload {
    public static final Id<CubesideDataS2C> PACKET_ID = new Id<>(new Identifier("cubesidemod", "data"));
    public static final PacketCodec<PacketByteBuf, CubesideDataS2C> PACKET_CODEC = PacketCodec.of(CubesideDataS2C::write, CubesideDataS2C::new);

    private ChatInfo chatInfo;
    private ScreenFlashInfo screenFlashInfo;

    public CubesideDataS2C(PacketByteBuf packet) {
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
                    String currentChannelColorString = packet.readString();
                    String currentPrivateChatPrefixString = packet.readString();
                    String currentResponsePartnerPrefixString = packet.readString();

                    currentChannelColor = Text.Serialization.fromJson(currentChannelColorString, DynamicRegistryManager.EMPTY);
                    currentPrivateChatPrefix = Text.Serialization.fromJson(currentPrivateChatPrefixString, DynamicRegistryManager.EMPTY);
                    currentResponsePartnerPrefix = Text.Serialization.fromJson(currentResponsePartnerPrefixString, DynamicRegistryManager.EMPTY);
                } catch (IndexOutOfBoundsException ignored) {
                }

                chatInfo = new ChatInfo(currentChannelName, currentPrivateChat, currentResponsePartner, currentChannelColor, currentPrivateChatPrefix, currentResponsePartnerPrefix);
            }

            if (cubesideDateChannel == challengeFlashScreenDataChannelID && cubesideDateChannelVersion == 0) {
                int color = packet.readInt();
                int duration = packet.readInt();
                screenFlashInfo = new ScreenFlashInfo(duration, new Color(color));
            }
        } catch (Exception e) {
            CubesideClientFabric.LOGGER.warn("Unable to read CubesideMod data", e);
        }
    }

    public void write(PacketByteBuf buf) {
        // nix write
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public ChatInfo getChatInfo() {
        return chatInfo;
    }

    public ScreenFlashInfo getScreenFlashInfo() {
        return screenFlashInfo;
    }

    public record ScreenFlashInfo(int duration, Color color) {
    }
}
