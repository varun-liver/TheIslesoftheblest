package com.isles.network;

import com.isles.client.cutscene.CutsceneClientState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class CutsceneEndS2CPacket {
    public CutsceneEndS2CPacket() {}

    public static void encode(CutsceneEndS2CPacket msg, FriendlyByteBuf buf) {
        // no payload
    }

    public static CutsceneEndS2CPacket decode(FriendlyByteBuf buf) {
        return new CutsceneEndS2CPacket();
    }

    public static void handle(CutsceneEndS2CPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CutsceneClientState.beginEnd(20)));
        ctx.setPacketHandled(true);
    }
}
