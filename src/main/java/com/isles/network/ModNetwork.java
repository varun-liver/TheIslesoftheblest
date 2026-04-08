package com.isles.network;

import com.isles.blest;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetwork {
    private ModNetwork() {}

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        ResourceLocation.fromNamespaceAndPath(blest.MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    private static int nextId = 0;
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;

        CHANNEL.messageBuilder(CutsceneStartS2CPacket.class, nextId++)
            .encoder(CutsceneStartS2CPacket::encode)
            .decoder(CutsceneStartS2CPacket::decode)
            .consumerMainThread(CutsceneStartS2CPacket::handle)
            .add();

        CHANNEL.messageBuilder(CutsceneEndS2CPacket.class, nextId++)
            .encoder(CutsceneEndS2CPacket::encode)
            .decoder(CutsceneEndS2CPacket::decode)
            .consumerMainThread(CutsceneEndS2CPacket::handle)
            .add();

        CHANNEL.messageBuilder(CutsceneOverlayDoneC2SPacket.class, nextId++)
            .encoder(CutsceneOverlayDoneC2SPacket::encode)
            .decoder(CutsceneOverlayDoneC2SPacket::decode)
            .consumerMainThread(CutsceneOverlayDoneC2SPacket::handle)
            .add();
    }

    public static void sendToPlayer(ServerPlayer player, Object message) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
