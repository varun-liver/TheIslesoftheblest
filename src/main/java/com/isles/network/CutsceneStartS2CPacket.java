package com.isles.network;

import com.isles.client.cutscene.CutsceneClientState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class CutsceneStartS2CPacket {
    public final int totalTicks;
    public final int rendererChangeTicks;
    public final int card1Ticks;
    public final int gap1Ticks;
    public final int card2Ticks;
    public final int gap2Ticks;
    public final int card3Ticks;
    public final int gap3Ticks;
    public final int card4Ticks;
    public final int gap4Ticks;
    public final int scrollTicks;

    public final String card1Title;
    public final String card1Subtitle;
    public final String card2Title;
    public final String card2Subtitle;
    public final String card3Title;
    public final String card3Subtitle;
    public final String card4Title;
    public final String card4Subtitle;
    public final String scrollText;

    public CutsceneStartS2CPacket(
        int totalTicks,
        int rendererChangeTicks,
        int card1Ticks,
        int gap1Ticks,
        int card2Ticks,
        int gap2Ticks,
        int card3Ticks,
        int gap3Ticks,
        int card4Ticks,
        int gap4Ticks,
        int scrollTicks,
        String card1Title,
        String card1Subtitle,
        String card2Title,
        String card2Subtitle,
        String card3Title,
        String card3Subtitle,
        String card4Title,
        String card4Subtitle,
        String scrollText
    ) {
        this.totalTicks = totalTicks;
        this.rendererChangeTicks = rendererChangeTicks;
        this.card1Ticks = card1Ticks;
        this.gap1Ticks = gap1Ticks;
        this.card2Ticks = card2Ticks;
        this.gap2Ticks = gap2Ticks;
        this.card3Ticks = card3Ticks;
        this.gap3Ticks = gap3Ticks;
        this.card4Ticks = card4Ticks;
        this.gap4Ticks = gap4Ticks;
        this.scrollTicks = scrollTicks;
        this.card1Title = card1Title == null ? "" : card1Title;
        this.card1Subtitle = card1Subtitle == null ? "" : card1Subtitle;
        this.card2Title = card2Title == null ? "" : card2Title;
        this.card2Subtitle = card2Subtitle == null ? "" : card2Subtitle;
        this.card3Title = card3Title == null ? "" : card3Title;
        this.card3Subtitle = card3Subtitle == null ? "" : card3Subtitle;
        this.card4Title = card4Title == null ? "" : card4Title;
        this.card4Subtitle = card4Subtitle == null ? "" : card4Subtitle;
        this.scrollText = scrollText == null ? "" : scrollText;
    }

    public static void encode(CutsceneStartS2CPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.totalTicks);
        buf.writeVarInt(msg.rendererChangeTicks);
        buf.writeVarInt(msg.card1Ticks);
        buf.writeVarInt(msg.gap1Ticks);
        buf.writeVarInt(msg.card2Ticks);
        buf.writeVarInt(msg.gap2Ticks);
        buf.writeVarInt(msg.card3Ticks);
        buf.writeVarInt(msg.gap3Ticks);
        buf.writeVarInt(msg.card4Ticks);
        buf.writeVarInt(msg.gap4Ticks);
        buf.writeVarInt(msg.scrollTicks);
        buf.writeUtf(msg.card1Title, 256);
        buf.writeUtf(msg.card1Subtitle, 256);
        buf.writeUtf(msg.card2Title, 256);
        buf.writeUtf(msg.card2Subtitle, 256);
        buf.writeUtf(msg.card3Title, 256);
        buf.writeUtf(msg.card3Subtitle, 256);
        buf.writeUtf(msg.card4Title, 256);
        buf.writeUtf(msg.card4Subtitle, 256);
        // allow more text; FriendlyByteBuf enforces max length
        buf.writeUtf(msg.scrollText, 8192);
    }

    public static CutsceneStartS2CPacket decode(FriendlyByteBuf buf) {
        int totalTicks = buf.readVarInt();
        int rendererChangeTicks = buf.readVarInt();
        int card1Ticks = buf.readVarInt();
        int gap1Ticks = buf.readVarInt();
        int card2Ticks = buf.readVarInt();
        int gap2Ticks = buf.readVarInt();
        int card3Ticks = buf.readVarInt();
        int gap3Ticks = buf.readVarInt();
        int card4Ticks = buf.readVarInt();
        int gap4Ticks = buf.readVarInt();
        int scrollTicks = buf.readVarInt();
        String card1Title = buf.readUtf(256);
        String card1Subtitle = buf.readUtf(256);
        String card2Title = buf.readUtf(256);
        String card2Subtitle = buf.readUtf(256);
        String card3Title = buf.readUtf(256);
        String card3Subtitle = buf.readUtf(256);
        String card4Title = buf.readUtf(256);
        String card4Subtitle = buf.readUtf(256);
        String scrollText = buf.readUtf(8192);
        return new CutsceneStartS2CPacket(
            totalTicks,
            rendererChangeTicks,
            card1Ticks,
            gap1Ticks,
            card2Ticks,
            gap2Ticks,
            card3Ticks,
            gap3Ticks,
            card4Ticks,
            gap4Ticks,
            scrollTicks,
            card1Title,
            card1Subtitle,
            card2Title,
            card2Subtitle,
            card3Title,
            card3Subtitle,
            card4Title,
            card4Subtitle,
            scrollText
        );
    }

    public static void handle(CutsceneStartS2CPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            CutsceneClientState.start(
                msg.totalTicks,
                msg.rendererChangeTicks,
                msg.card1Ticks,
                msg.gap1Ticks,
                msg.card2Ticks,
                msg.gap2Ticks,
                msg.card3Ticks,
                msg.gap3Ticks,
                msg.card4Ticks,
                msg.gap4Ticks,
                msg.scrollTicks,
                msg.card1Title,
                msg.card1Subtitle,
                msg.card2Title,
                msg.card2Subtitle,
                msg.card3Title,
                msg.card3Subtitle,
                msg.card4Title,
                msg.card4Subtitle,
                msg.scrollText
            );
        }));
        ctx.setPacketHandled(true);
    }
}
