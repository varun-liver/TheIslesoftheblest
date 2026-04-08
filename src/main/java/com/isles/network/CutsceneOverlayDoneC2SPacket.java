package com.isles.network;

import com.isles.ArmorStandCutsceneManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Sent by the client when the scroll text has fully left the screen,
 * so the server can end the cutscene immediately (restore camera/armor, etc).
 */
public final class CutsceneOverlayDoneC2SPacket {
    public CutsceneOverlayDoneC2SPacket() {}

    public static void encode(CutsceneOverlayDoneC2SPacket msg, FriendlyByteBuf buf) {
        // no payload
    }

    public static CutsceneOverlayDoneC2SPacket decode(FriendlyByteBuf buf) {
        return new CutsceneOverlayDoneC2SPacket();
    }

    public static void handle(CutsceneOverlayDoneC2SPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ServerPlayer sender = ctx.getSender();
        if (sender != null) {
            ctx.enqueueWork(() -> ArmorStandCutsceneManager.requestEnd(sender));
        }
        ctx.setPacketHandled(true);
    }
}

