package com.isles;

public class CutsceneContent {
    // Overlay script (client). Keep this ASCII-only.
    public static final String CARD1_TITLE = "The Infection";
    public static final String CARD1_SUBTITLE = "It ends...";
    public static final String CARD2_TITLE = "Somewhere Above";
    public static final String CARD2_SUBTITLE = "A forgotten promise stirs.";
    public static final String CARD3_TITLE = "It Is finished";
    public static final String CARD3_SUBTITLE = "After 5 eons";
    public static final String CARD4_TITLE = "Or Is it...";
    public static final String CARD4_SUBTITLE = "";
    public static final String SCROLL_TEXT = String.join("\n",
            "The Isles Of The Blest",
            "\u00A72Where am I?\n",
            "\u00A71It might be too late\n",
            "\u00A72What do you mean\n",
            "\u00A71Your Success only made them stronger\n",
            "\u00A71There was a age before you,\n",
            "\u00A71Before everything\n",
            "\u00A71You must defeat them\n",
            "\u00A72Defeat who?\n",
            "\u00A72Who Are You?\n",
            "\u00A71The Real question is, who are you {player}"
    );

    public static final int CARD1_TICKS = 50; // 2.5s
    public static final int GAP1_TICKS = 10;  // 0.5s
    public static final int CARD2_TICKS = 50; // 2.5s
    public static final int GAP2_TICKS = 10;  // 0.5s
    public static final int CARD3_TICKS = 50; // 2.5s
    public static final int GAP3_TICKS = 10;
    public static final int CARD4_TICKS = 50; // 2.5s
    public static final int GAP4_TICKS = 10;
    public static final int RENDERER_CHANGE_TICKS = 100; // 5 seconds of renderer change
    // Minimum scroll time; actual scroll duration is estimated from content length so it doesn't cut off early.
    public static final int SCROLL_TICKS = 240; // 12s minimum

    public static int estimateScrollTicks(String text) {
        if (text == null || text.isEmpty()) return SCROLL_TICKS;
        int lines = text.split("\\R", -1).length;
        // Rough heuristic: longer text needs more time. This is deliberately conservative.
        int ticks = 160 + (lines * 35) + (text.length() / 4);
        int min = SCROLL_TICKS;
        int max = 20 * 60 * 20; // 20 minutes hard cap
        if (ticks < min) ticks = min;
        if (ticks > max) ticks = max;
        return ticks;
    }
}
