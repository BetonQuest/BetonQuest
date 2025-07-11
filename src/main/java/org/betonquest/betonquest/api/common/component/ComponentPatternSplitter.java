package org.betonquest.betonquest.api.common.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The ComponentPatternSplitter class provides functionality to split a Component by a specified pattern,
 * while keeping the style and hierarchy of the original component intact.
 */
@SuppressWarnings("PMD.LooseCoupling")
public final class ComponentPatternSplitter {
    private ComponentPatternSplitter() {
    }

    /**
     * Splits a Component into multiple lines based on a specified pattern.
     *
     * @param component the Component to split
     * @param splitter  the Pattern to split the Component by
     * @param compact   if true, each line will be compacted to simplify style and hierarchy
     * @return a list of Components, each representing a line
     */
    public static LinkedList<Component> split(final Component component, final Pattern splitter, final boolean compact) {
        final LinkedList<Component> split = split(component, splitter);
        if (compact) {
            return split.stream()
                    .map(Component::compact)
                    .map(Component::compact)
                    .collect(Collectors.toCollection(LinkedList::new));
        }
        return split;
    }

    /**
     * Splits a Component into multiple lines based on a specified pattern.
     *
     * @param component the Component to split
     * @param splitter  the Pattern to split the Component by
     * @return a list of Components, each representing a line
     */
    public static LinkedList<Component> split(final Component component, final Pattern splitter) {
        final LinkedList<Component> result = new LinkedList<>();
        if (component instanceof final TextComponent textComponent) {
            final LinkedList<String> parts = split(splitter, textComponent.content());
            parts.forEach(part -> result.add(Component.text(part).style(component.style())));
        }
        final TextComponent parentComponent = Component.empty().style(component.style());
        for (final Component child : component.children()) {
            final Component last = result.removeLast();
            final LinkedList<Component> parts = split(child, splitter);
            final Component firstPart = parts.removeFirst();
            result.add(isEmpty(firstPart) ? last : last.append(firstPart));
            for (final Component part : parts) {
                result.add(parentComponent.append(part));
            }
        }
        return result;
    }

    @VisibleForTesting
    static LinkedList<String> split(final Pattern splitter, final String text) {
        final LinkedList<String> parts = new LinkedList<>();
        final Matcher matcher = splitter.matcher(text);

        int last = 0;
        while (matcher.find()) {
            final String part = text.substring(last, matcher.start());
            parts.add(part);
            last = matcher.end();
        }

        parts.add(text.substring(last));
        return parts;
    }

    private static boolean isEmpty(final Component component) {
        return component instanceof final TextComponent textComponent
                && textComponent.content().isEmpty() && textComponent.children().isEmpty();
    }
}
