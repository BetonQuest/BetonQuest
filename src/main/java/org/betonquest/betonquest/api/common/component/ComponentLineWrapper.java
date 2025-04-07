package org.betonquest.betonquest.api.common.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The ComponentLineWrapper class is responsible for splitting a Component into multiple lines.
 */
public final class ComponentLineWrapper {
    /**
     * The newline string to separate title and subtitle.
     */
    private static final Pattern NEW_LINE = Pattern.compile("\n");

    private ComponentLineWrapper() {
    }

    /**
     * Splits a Component into multiple lines based on new line characters.
     *
     * @param self the Component to split
     * @return a list of Components, each representing a line
     */
    @SuppressWarnings("PMD.LooseCoupling")
    public static List<Component> splitNewLine(final Component self) {
        final LinkedList<Component> lines = splitNewLineTextComponent(self);
        if (self.children().isEmpty()) {
            return lines;
        }
        Component last = lines.removeLast();
        for (final Component child : self.children()) {
            final LinkedList<Component> childSegments = (LinkedList<Component>) splitNewLine(child);
            last = last.append(childSegments.removeFirst());
            for (final Component component : childSegments) {
                lines.add(last.compact());
                last = Component.empty().style(last.style()).append(component);
            }
        }
        lines.add(last.compact());
        return lines;
    }

    @SuppressWarnings("PMD.LooseCoupling")
    private static LinkedList<Component> splitNewLineTextComponent(final Component component) {
        if (!(component instanceof final TextComponent text)) {
            return new LinkedList<>(List.of(component));
        }
        final String content = text.content();
        final List<String> segments = new ArrayList<>(List.of(NEW_LINE.split(content, -1)));
        return segments.stream()
                .map(segment -> Component.text(segment).style(text.style()))
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
