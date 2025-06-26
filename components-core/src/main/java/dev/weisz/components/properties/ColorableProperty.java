package dev.weisz.components.properties;

import dev.mccue.color.terminal.TerminalColor;
import org.jspecify.annotations.Nullable;

/**
 * Represents anything that should be colored, text, borders, unicodes ect.
 */
public interface ColorableProperty {
    @Nullable
    TerminalColor color();
}
