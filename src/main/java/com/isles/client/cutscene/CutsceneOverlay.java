package com.isles.client.cutscene;

import com.isles.blest;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.isles.network.CutsceneOverlayDoneC2SPacket;
import com.isles.network.ModNetwork;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = blest.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CutsceneOverlay {
    private CutsceneOverlay() {}

    private static int cachedWrapWidth = -1;
    private static String cachedScrollText = "";
    private static List<FormattedCharSequence> cachedWrappedScroll = List.of();
    private static boolean scrollDoneSent = false;
    private static boolean shaderActive = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        boolean wasActive = CutsceneClientState.isActive();
        CutsceneClientState.clientTick();

        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;

        if (wasActive && !CutsceneClientState.isActive()) {
            // Cutscene just ended
            scrollDoneSent = false;
            shaderActive = false;
            if (mc.gameRenderer != null) {
                mc.tell(() -> mc.gameRenderer.shutdownEffect());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (!CutsceneClientState.isActive()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;

        // Ensure grayscale shader is loaded when the cutscene is active
        if (!shaderActive && mc.gameRenderer != null) {
            shaderActive = true;
            mc.tell(() -> {
                mc.gameRenderer.loadEffect(new net.minecraft.resources.ResourceLocation("minecraft", "shaders/post/desaturate.json"));
            });
        }

        
        int total = CutsceneClientState.getTotalTicks();
        int elapsed = CutsceneClientState.getElapsedTicks();
        float partial = mc.getFrameTime();
        CutsceneClientState.setPartialTicks(partial);

        GuiGraphics gg = event.getGuiGraphics();
        int w = gg.guiWidth();
        int h = gg.guiHeight();

        int rendererChangeTicks = CutsceneClientState.getRendererChangeTicks();
        float now = elapsed + partial;

        // Fade in/out to black; stays fully black during the cutscene body.
        // We start the fade in AFTER the renderer change phase.
        float overlayNow = now - rendererChangeTicks;
        int overlayTotal = total - rendererChangeTicks;
        
        int fadeTicks = Math.max(1, Math.min(20, overlayTotal / 4));
        float fadeIn = clamp01(overlayNow / fadeTicks);
        float fadeOut = clamp01((overlayTotal - overlayNow) / fadeTicks);
        float blackAlpha = smoothstep(Math.min(fadeIn, fadeOut));

        // During the animation phase, add a dedicated fade to black from tick 30 to tick 40.
        float animationNow = now;
        if (animationNow >= 30.0f && animationNow <= 40.0f) {
            float animFade = smoothstep(clamp01((animationNow - 30.0f) / 10.0f));
            blackAlpha = Math.max(blackAlpha, animFade);
        } else if (animationNow > 40.0f && animationNow < rendererChangeTicks) {
            blackAlpha = Math.max(blackAlpha, 1.0f);
        }

        int a = (int) (blackAlpha * 255.0f);
        int argb = (a << 24); // black with alpha
        gg.fill(0, 0, w, h, argb);

        Font font = mc.font;

        if (CutsceneClientState.isEnding()) {
            return;
        }

        int card1Ticks = CutsceneClientState.getCard1Ticks();
        int gap1Ticks = CutsceneClientState.getGap1Ticks();
        int card2Ticks = CutsceneClientState.getCard2Ticks();
        int gap2Ticks = CutsceneClientState.getGap2Ticks();
        int card3Ticks = CutsceneClientState.getCard3Ticks();
        int gap3Ticks = CutsceneClientState.getGap3Ticks();
        int card4Ticks = CutsceneClientState.getCard4Ticks();
        int gap4Ticks = CutsceneClientState.getGap4Ticks();
        int scrollTicks = CutsceneClientState.getScrollTicks();

        int tCard1Start = 0;
        int tGap1Start = tCard1Start + card1Ticks;
        int tCard2Start = tGap1Start + gap1Ticks;
        int tGap2Start = tCard2Start + card2Ticks;
        int tCard3Start = tGap2Start + gap2Ticks;
        int tGap3Start = tCard3Start + card3Ticks;
        int tCard4Start = tGap3Start + gap3Ticks;
        int tGap4Start = tCard4Start + card4Ticks;
        int tScrollStart = tGap4Start + gap4Ticks;
        int tScrollEnd = tScrollStart + scrollTicks;

        if (overlayNow >= tCard1Start && overlayNow < tGap1Start) {
            drawCard(gg, font, w, h, overlayNow - tCard1Start, card1Ticks, CutsceneClientState.getCard1Title(), CutsceneClientState.getCard1Subtitle());
            return;
        }
        if (overlayNow >= tCard2Start && overlayNow < tGap2Start) {
            drawCard(gg, font, w, h, overlayNow - tCard2Start, card2Ticks, CutsceneClientState.getCard2Title(), CutsceneClientState.getCard2Subtitle());
            return;
        }
        if (overlayNow >= tCard3Start && overlayNow < tGap3Start) {
            drawCard(gg, font, w, h, overlayNow - tCard3Start, card3Ticks, CutsceneClientState.getCard3Title(), CutsceneClientState.getCard3Subtitle());
            return;
        }
        if (overlayNow >= tCard4Start && overlayNow < tGap4Start) {
            drawCard(gg, font, w, h, overlayNow - tCard4Start, card4Ticks, CutsceneClientState.getCard4Title(), CutsceneClientState.getCard4Subtitle());
            return;
        }
        if (overlayNow >= tScrollStart && overlayNow < tScrollEnd) {
            drawScroll(gg, font, w, h, overlayNow - tScrollStart, scrollTicks, CutsceneClientState.getScrollText());
        }
    }

    private static void drawCard(GuiGraphics gg, Font font, int w, int h, float localT, int cardTicks, String title, String subtitle) {
        if ((title == null || title.isEmpty()) && (subtitle == null || subtitle.isEmpty())) return;
        if (cardTicks <= 0) return;

        int fade = Math.max(1, Math.min(10, cardTicks / 4));
        float fadeIn = clamp01(localT / fade);
        float fadeOut = clamp01((cardTicks - localT) / fade);
        float textAlpha = smoothstep(Math.min(fadeIn, fadeOut));
        int ta = (int) (textAlpha * 255.0f);
        int titleColor = (ta << 24) | 0xFFFFFF;
        int subColor = (ta << 24) | 0xDDDDDD;

        int baseY = (int) (h * 0.68f);
        if (title != null && !title.isEmpty()) {
            int tw = font.width(title);
            gg.drawString(font, title, (w - tw) / 2, baseY - 12, titleColor, true);
        }
        if (subtitle != null && !subtitle.isEmpty()) {
            int sw = font.width(subtitle);
            gg.drawString(font, subtitle, (w - sw) / 2, baseY + 2, subColor, true);
        }
    }

    private static void drawScroll(GuiGraphics gg, Font font, int w, int h, float localT, int scrollTicks, String scrollText) {
        if (scrollTicks <= 0) return;
        if (scrollText == null || scrollText.isEmpty()) return;

        int wrapWidth = Math.min(360, Math.max(220, w - 80));
        if (wrapWidth != cachedWrapWidth || !scrollText.equals(cachedScrollText)) {
            cachedWrapWidth = wrapWidth;
            cachedScrollText = scrollText;
            List<FormattedCharSequence> lines = new ArrayList<>();
            for (String rawLine : scrollText.split("\\R", -1)) {
                if (rawLine.isEmpty()) {
                    lines.add(FormattedCharSequence.EMPTY);
                    continue;
                }
                // Supports legacy formatting codes like \u00A7c (red), \u00A7l (bold), \u00A7r (reset).
                lines.addAll(font.split(parseLegacyFormatted(rawLine), wrapWidth));
            }
            cachedWrappedScroll = List.copyOf(lines);
        }

        // Fade the scroll text in and out a bit.
        int fade = Math.max(1, Math.min(20, scrollTicks / 6));
        float fadeIn = clamp01(localT / fade);
        float fadeOut = clamp01((scrollTicks - localT) / fade);
        float textAlpha = smoothstep(Math.min(fadeIn, fadeOut));

        // Credits-like scroll: start below the screen, move upward.
        float seconds = localT / 20.0f;
        float pxPerSecond = 14.0f;
        float startY = h + 24.0f;
        float y0 = startY - seconds * pxPerSecond;

        int lineHeight = font.lineHeight + 2;
        int x = (w - wrapWidth) / 2;

        // Apply fade alpha to all text (including colored segments).
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, clamp01(textAlpha));
        for (int i = 0; i < cachedWrappedScroll.size(); i++) {
            float y = y0 + i * lineHeight;
            if (y < -lineHeight) continue;
            if (y > h + lineHeight) continue;
            gg.drawString(font, cachedWrappedScroll.get(i), x, (int) y, 0xFFFFFF, true);
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        // If the last line has fully left the screen, end the overlay early.
        if (!scrollDoneSent && !cachedWrappedScroll.isEmpty()) {
            float lastY = y0 + (cachedWrappedScroll.size() - 1) * lineHeight;
            if (lastY < -lineHeight) {
                scrollDoneSent = true;
                // Ask the server to end the cutscene (restore camera, etc).
                ModNetwork.CHANNEL.sendToServer(new CutsceneOverlayDoneC2SPacket());
                // Fade out the black overlay locally.
                CutsceneClientState.beginEnd(20);
            }
        }
    }

    private static Component parseLegacyFormatted(String s) {
        if (s == null || s.isEmpty()) return Component.empty();

        MutableComponent out = Component.empty();
        Style style = Style.EMPTY;

        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '\u00A7' && i + 1 < s.length()) {
                // Flush any buffered plain text under the current style.
                if (buf.length() > 0) {
                    out.append(Component.literal(buf.toString()).withStyle(style));
                    buf.setLength(0);
                }

                char code = Character.toLowerCase(s.charAt(i + 1));
                i++; // consume code

                ChatFormatting fmt = ChatFormatting.getByCode(code);
                if (fmt == null) {
                    continue;
                }

                if (fmt == ChatFormatting.RESET) {
                    style = Style.EMPTY;
                    continue;
                }

                if (fmt.isColor()) {
                    style = style.withColor(fmt);
                    // Bedrock/legacy behavior: setting a color clears other formatting.
                    style = style.withBold(false).withItalic(false).withUnderlined(false).withStrikethrough(false).withObfuscated(false);
                    continue;
                }

                // Formatting codes.
                if (fmt == ChatFormatting.BOLD) style = style.withBold(true);
                else if (fmt == ChatFormatting.ITALIC) style = style.withItalic(true);
                else if (fmt == ChatFormatting.UNDERLINE) style = style.withUnderlined(true);
                else if (fmt == ChatFormatting.STRIKETHROUGH) style = style.withStrikethrough(true);
                else if (fmt == ChatFormatting.OBFUSCATED) style = style.withObfuscated(true);
            } else {
                buf.append(ch);
            }
        }

        if (buf.length() > 0) {
            out.append(Component.literal(buf.toString()).withStyle(style));
        }
        return out;
    }

    private static float clamp01(float v) {
        if (v < 0f) return 0f;
        if (v > 1f) return 1f;
        return v;
    }

    private static float smoothstep(float v) {
        // classic smoothstep
        return v * v * (3f - 2f * v);
    }
}
