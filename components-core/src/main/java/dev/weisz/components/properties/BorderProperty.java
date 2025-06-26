package dev.weisz.components.properties;

import dev.mccue.color.terminal.TerminalColor;
import org.jspecify.annotations.Nullable;

/**
 * Used to mark any properties that are used to render the border, ie dashed, solid, color, ect
 */
public record BorderProperty(
        BorderType type,
        BorderStyle style,
        @Nullable TerminalColor color
) implements Property, ColorableProperty {

    private sealed interface BorderStyle permits SolidBorderStyle, DashedBorderStyle { }

    /**
     *
     * @param thickness - How thick in columns/rows the border should be.
     */
    public record SolidBorderStyle(int thickness) implements BorderStyle {
        /**
         * Gives a solid border style with a default thickness of 1.
         */
        public SolidBorderStyle() {
            this(1);
        }
    }

    /**
     * The border style with a dashed effect.
     * Example:
     * var alternatingBorderStyle = new DashedBorderStyle(1, 0.5);
     * turns into - - - - - - (a dash appears 0.5 of the time)
     *
     * var infrequentBorderStyle = new DashedBorderStyle(1, 0.1);
     * turns into -         - (a dash appears 1 in every 10 spaces)
     *
     * @param thickness - How thick in columns/rows the border should be.
     * @param frequency - a number between 0 and 1 (exclusive) indicating how many spaces there should be.
     */
    public record DashedBorderStyle(int thickness, double frequency) implements BorderStyle {}

    @Override
    public @Nullable TerminalColor color() {
        return color;
    }

}
