package de.fanta.cubeside.event;

import de.fanta.cubeside.CubesideClientFabric;
import de.fanta.cubeside.packets.CubesideRankInfoS2C;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class RankDataChannelHandler implements ClientPlayNetworking.PlayPayloadHandler<CubesideRankInfoS2C>, ClientConfigurationNetworking.ConfigurationPayloadHandler<CubesideRankInfoS2C>  {

    public RankDataChannelHandler() {
        PayloadTypeRegistry.playS2C().register(CubesideRankInfoS2C.PACKET_ID, CubesideRankInfoS2C.PACKET_CODEC);
        PayloadTypeRegistry.configurationS2C().register(CubesideRankInfoS2C.PACKET_ID, CubesideRankInfoS2C.PACKET_CODEC);

        ClientPlayNetworking.registerGlobalReceiver(CubesideRankInfoS2C.PACKET_ID, this);
        ClientConfigurationNetworking.registerGlobalReceiver(CubesideRankInfoS2C.PACKET_ID, this);
    }

    @Override
    public void receive(CubesideRankInfoS2C payload, ClientConfigurationNetworking.Context context) {
        CubesideClientFabric.setRank(payload.rank());
    }

    @Override
    public void receive(CubesideRankInfoS2C payload, ClientPlayNetworking.Context context) {
        CubesideClientFabric.setRank(payload.rank());
    }
}
