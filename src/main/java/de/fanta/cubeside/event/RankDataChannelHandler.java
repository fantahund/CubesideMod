package de.fanta.cubeside.event;

import de.fanta.cubeside.CubesideClientFabric;
import io.netty.handler.codec.DecoderException;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.nio.charset.Charset;

public class RankDataChannelHandler { //implements ClientPlayNetworking.PlayChannelHandler {

    /*public static final Identifier CHANNEL_IDENTIFIER = new Identifier("cubesidemod", "rank");

    public RankDataChannelHandler() {
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL_IDENTIFIER, this);
    }

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf packet, PacketSender sender) {
        try {
            String rank = packet.toString(Charset.defaultCharset());
            CubesideClientFabric.setRank(rank);
        } catch (DecoderException e) {
            CubesideClientFabric.LOGGER.warn("Unable to decode rank data", e);
        }
    }*/
}
