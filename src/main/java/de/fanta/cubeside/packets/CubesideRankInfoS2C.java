package de.fanta.cubeside.packets;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public record CubesideRankInfoS2C(String rank) implements CustomPayload {
    public static final Id<CubesideRankInfoS2C> PACKET_ID = new Id<>(new Identifier("cubesidemod", "rank"));
    public static final PacketCodec<PacketByteBuf, CubesideRankInfoS2C> PACKET_CODEC = PacketCodec.of(CubesideRankInfoS2C::write, CubesideRankInfoS2C::new);

    public CubesideRankInfoS2C(PacketByteBuf buf) {
        this(parse(buf));
    }

    private static String parse(PacketByteBuf buf) {
        return buf.toString(Charset.defaultCharset());
    }

    public void write(PacketByteBuf buf) {
        byte[] bytes = rank.getBytes(StandardCharsets.UTF_8);
        //buf.writeByte(bytes.length);
        buf.writeBytes(bytes);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
