package de.fanta.cubeside.packets;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import de.fanta.cubeside.CubesideClientFabric;
import de.fanta.cubeside.util.ChatInfo;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryOps;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.StrictJsonParser;

public class CubesideDataS2C implements CustomPayload {
    public static final Id<CubesideDataS2C> PACKET_ID = new Id<>(Identifier.of("cubesidemod", "data"));
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

                    RegistryOps<JsonElement> ops = MinecraftClient.getInstance().world.getRegistryManager().getOps(JsonOps.INSTANCE);
                    JsonElement jsonElement = StrictJsonParser.parse(currentChannelColorString);
                    DataResult<Pair<Text, JsonElement>> result = TextCodecs.CODEC.decode(ops, jsonElement);
                    if (result.isSuccess()) {
                        currentChannelColor = result.getOrThrow().getFirst().copy();
                    }
                    jsonElement = StrictJsonParser.parse(currentPrivateChatPrefixString);
                    result = TextCodecs.CODEC.decode(ops, jsonElement);
                    if (result.isSuccess()) {
                        currentPrivateChatPrefix = result.getOrThrow().getFirst().copy();
                    }
                    jsonElement = StrictJsonParser.parse(currentResponsePartnerPrefixString);
                    result = TextCodecs.CODEC.decode(ops, jsonElement);
                    if (result.isSuccess()) {
                        currentResponsePartnerPrefix = result.getOrThrow().getFirst().copy();
                    }
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

    public record ScreenFlashInfo(int duration, Color color) {}
}
