package org.betonquest.betonquest.api.common.component;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;

import java.util.List;
import java.util.function.Supplier;

/**
 * A wrapper for Components that splits them into multiple lines based on a fixed width.
 */
public class FixedComponentLineWrapper extends ComponentLineWrapper {

    /**
     * The width of the line in pixels.
     */
    private final int maxLineWidth;

    /**
     * Creates a new ComponentLineWrapper instance.
     *
     * @param fontRegistry the font registry to use
     * @param maxLineWidth the maximum width of a line in pixels
     */
    public FixedComponentLineWrapper(final FontRegistry fontRegistry, final int maxLineWidth) {
        super(fontRegistry);
        this.maxLineWidth = maxLineWidth;
    }

    /**
     * Wraps a Component into multiple lines based on the specified line width.
     *
     * @param component the Component to wrap
     * @return a list of Components, each representing a line
     */
    public List<Component> splitWidth(final Component component) {
        return splitWidth(component, maxLineWidth);
    }

    /**
     * Wraps a Component into multiple lines based on the specified line width.
     *
     * @param component  the Component to wrap
     * @param linePrefix a Supplier for the prefix of each line
     * @return a list of Components, each representing a line
     */
    public List<Component> splitWidth(final Component component, final Supplier<Component> linePrefix) {
        return splitWidth(component, linePrefix, maxLineWidth);
    }

    /**
     * Gets the maximum line width in pixels.
     *
     * @return the maximum line width in pixels
     */
    public int getMaxLineWidth() {
        return maxLineWidth;
    }
}
