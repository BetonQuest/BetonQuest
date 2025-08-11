package org.betonquest.betonquest.api.common.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A wrapper for Components that splits them into multiple lines based on a fixed width and a maximum number of lines.
 * This is typically used for formatting book pages in a way that respects both the width and height constraints.
 */
public class BookPageWrapper extends FixedComponentLineWrapper {
    /**
     * The pattern used to identify new pages in the component text.
     */
    private static final Pattern NEW_PAGE = Pattern.compile("(?<!\\\\)(?:\\\\\\\\)*(\\|)");

    /**
     * The maximum number of lines on a page.
     */
    private final int maxLines;

    /**
     * Creates a new ComponentLineWrapper instance.
     *
     * @param fontRegistry the font registry to use
     * @param maxLineWidth the maximum width of a line in pixels
     * @param maxLines     the maximum number of lines on a page
     */
    public BookPageWrapper(final FontRegistry fontRegistry, final int maxLineWidth, final int maxLines) {
        super(fontRegistry, maxLineWidth);
        this.maxLines = maxLines;
    }

    /**
     * Splits a Component into multiple pages based on the specified new page pattern and maximum number of lines.
     *
     * @param component the Component to split into pages
     * @return a list of Components, each representing a page
     */
    public List<Component> splitPages(final Component component) {
        final List<Component> pages = new ArrayList<>();
        for (final Component page : ComponentPatternSplitter.split(component, NEW_PAGE, true)) {
            final List<Component> wrappedLines = splitWidth(page);
            while (!wrappedLines.isEmpty()) {
                final List<Component> pageLines = getNextPage(wrappedLines);
                final Component cuttedPage = Component.join(JoinConfiguration.newlines(), pageLines);
                pages.add(cuttedPage);
            }
        }
        return pages;
    }

    private List<Component> getNextPage(final List<Component> wrappedLines) {
        final List<Component> pageLines = new ArrayList<>();
        while (pageLines.size() < maxLines && !wrappedLines.isEmpty()) {
            pageLines.add(wrappedLines.remove(0));
        }
        return pageLines;
    }
}
