package org.betonquest.betonquest.api.common.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.common.component.font.Font;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The ComponentLineWrapper class is responsible for splitting a Component into multiple lines,
 * while each line will be compacted in the most minimal style and hierarchy.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class ComponentLineWrapper {
    /**
     * The newline string to split text components by new lines.
     */
    private static final Pattern NEW_LINE = Pattern.compile("\n");

    /**
     * The leading space pattern to split text components by leading spaces.
     */
    private static final Pattern LEADING_SPACE = Pattern.compile("(?= )");

    /**
     * The space pattern to split text components by spaces.
     */
    private static final Pattern SPACE = Pattern.compile(" ");

    /**
     * The character pattern to split text components by each character.
     */
    private static final Pattern CHARACTER = Pattern.compile("(?!^)(?=.)");

    /**
     * The font registry used to get the width of the characters.
     */
    private final FontRegistry fontRegistry;

    /**
     * Creates a new ComponentLineWrapper instance.
     *
     * @param fontRegistry the font registry to use
     */
    public ComponentLineWrapper(final FontRegistry fontRegistry) {
        this.fontRegistry = fontRegistry;
    }

    /**
     * Splits a Component into multiple lines based on new line characters.
     *
     * @param component the Component to split
     * @return a list of Components, each representing a line
     */
    public static List<Component> splitNewLine(final Component component) {
        return ComponentPatternSplitter.split(component, NEW_LINE, true);
    }

    /**
     * Wraps a Component into multiple lines based on the specified line width.
     *
     * @param component    the Component to wrap
     * @param maxLineWidth the maximum width of a line in pixels
     * @return a list of Components, each representing a line
     */
    public List<Component> splitWidth(final Component component, final int maxLineWidth) {
        return splitWidth(component, Component::empty, maxLineWidth);
    }

    /**
     * Wraps a Component into multiple lines based on the specified line width.
     *
     * @param component    the Component to wrap
     * @param linePrefix   a Supplier for the prefix of each line
     * @param maxLineWidth the maximum width of a line in pixels
     * @return a list of Components, each representing a line
     */
    public List<Component> splitWidth(final Component component, final Supplier<Component> linePrefix, final int maxLineWidth) {
        final List<Component> newLineWrapped = splitNewLine(component);

        final List<Component> resolvedLinePrefix = new ArrayList<>();
        final Supplier<Integer> offsetProvider = () -> {
            final Component prefix = linePrefix.get();
            resolvedLinePrefix.add(prefix);
            return width(prefix);
        };

        final List<Component> lines = newLineWrapped.stream()
                .map(line -> wrap(line, new Offset(offsetProvider), maxLineWidth))
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(LinkedList::new));

        return appendPrefixes(resolvedLinePrefix, lines).stream()
                .map(Component::compact)
                .map(Component::compact)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private List<Component> appendPrefixes(final List<Component> prefixes, final List<Component> lines) {
        if (prefixes.size() != lines.size()) {
            throw new IllegalStateException("Prefixes and lines must have the same size.");
        }
        final List<Component> result = new ArrayList<>(lines.size());
        for (int i = 0; i < lines.size(); i++) {
            result.add(prefixes.get(i).append(lines.get(i)));
        }
        return result;
    }

    private List<Component> wrap(final Component component, final Offset offset, final int maxLineWidth) {
        final List<Pair<Component, Integer>> wordList = ComponentPatternSplitter.split(component, LEADING_SPACE).stream()
                .map(word -> Pair.of(word, width(word)))
                .collect(Collectors.toCollection(LinkedList::new));

        final List<Component> lines = new ArrayList<>();
        final ComponentBuilder line = new ComponentBuilder();
        for (final Pair<Component, Integer> entry : wordList) {
            final Component word = entry.getKey();
            final int wordWidth = entry.getValue();

            if (offset.getOffset() + wordWidth <= maxLineWidth) {
                line.append(word);
                offset.addOffset(wordWidth);
                continue;
            }

            if (wordWidth > maxLineWidth) {
                wrapWordExceedingLineLength(lines, line, offset, word, maxLineWidth);
                continue;
            }

            lines.add(line.build());
            final Component wordWithoutSpace = getComponentWithoutLeadingSpace(word);
            line.append(wordWithoutSpace);
            offset.reset();
            offset.addOffset(width(wordWithoutSpace));
        }
        if (!line.isEmpty()) {
            lines.add(line.build());
        }
        return lines;
    }

    private void wrapWordExceedingLineLength(final List<Component> lines, final ComponentBuilder line, final Offset offset,
                                             final Component word, final int maxLineWidth) {
        final List<Component> characters = ComponentPatternSplitter.split(word, CHARACTER);
        for (final Component character : characters) {
            final int characterWidth = width(character);
            if (offset.getOffset() + characterWidth <= maxLineWidth) {
                line.append(character);
                offset.addOffset(characterWidth);
                continue;
            }
            lines.add(line.build());
            final Component characterWithoutSpace = getComponentWithoutLeadingSpace(character);
            line.append(characterWithoutSpace);
            offset.reset();
            offset.addOffset(width(characterWithoutSpace));
        }
    }

    /**
     * Calculates the width of a Component in pixels.
     *
     * @param component the Component to calculate the width for
     * @return the width of the Component in pixels
     */
    public int width(final Component component) {
        return width(component, new ComponentDecorations(component));
    }

    private int width(final Component component, final ComponentDecorations decorations) {
        int width = 0;
        if (component instanceof final TextComponent text) {
            final Font font = fontRegistry.getFont(text.font());
            width = getTextWidth(font, text.content(), decorations);
        }
        return width + component.children().stream().mapToInt(child -> width(child, decorations.getChild(child))).sum();
    }

    private int getTextWidth(final Font font, final String text, final ComponentDecorations decorations) {
        int width = 0;
        final int decorationFix = getTextDecorationWidth(decorations);
        for (final char c : text.toCharArray()) {
            width += font.getWidth(c) + decorationFix;
        }
        return width;
    }

    private int getTextDecorationWidth(final ComponentDecorations decorations) {
        if (decorations.getDecorations().get(TextDecoration.BOLD) == TextDecoration.State.TRUE) {
            return 1;
        }
        return 0;
    }

    @SuppressWarnings("PMD.LooseCoupling")
    private Component getComponentWithoutLeadingSpace(final Component component) {
        final LinkedList<Component> split = ComponentPatternSplitter.split(component, SPACE);
        final Component first = split.removeFirst();
        if (split.isEmpty()) {
            return first;
        }
        return split.removeFirst();
    }

    /**
     * The Offset class is used to store the offset of a line in pixels.
     */
    private static class Offset {
        /**
         * The offset to use even if {@link #reset()} is called.
         */
        private final Supplier<Integer> provider;

        /**
         * The offset of the line in pixels.
         */
        private int value;

        /**
         * Creates a new Offset instance with the default value of 0.
         */
        public Offset(final Supplier<Integer> provider) {
            this.provider = provider;
            this.value = provider.get();
        }

        /**
         * Gets the current offset.
         *
         * @return the current offset
         */
        public int getOffset() {
            return value;
        }

        /**
         * Adds an offset to the current offset.
         *
         * @param offset the offset to add
         */
        public void addOffset(final int offset) {
            this.value += offset;
        }

        /**
         * Resets the offset to 0.
         */
        public void reset() {
            this.value = provider.get();
        }
    }

    /**
     * The ComponentDecorations class is used to store the decorations of a Component.
     */
    private static class ComponentDecorations {
        /**
         * The map of TextDecoration to its state for the Component.
         */
        private final Map<TextDecoration, TextDecoration.State> decorations;

        /**
         * Creates a new ComponentDecorations instance for the given Component.
         *
         * @param component the Component to get the decorations for
         */
        public ComponentDecorations(final Component component) {
            this.decorations = component.decorations();
        }

        private ComponentDecorations(final Map<TextDecoration, TextDecoration.State> parent, final Component child) {
            this.decorations = parent;
            for (final Map.Entry<TextDecoration, TextDecoration.State> entry : child.decorations().entrySet()) {
                final TextDecoration decoration = entry.getKey();
                final TextDecoration.State state = entry.getValue();
                if (state != TextDecoration.State.NOT_SET) {
                    this.decorations.put(decoration, state);
                }
            }
        }

        /**
         * Creates a new ComponentDecorations instance for a child Component.
         *
         * @param component the child Component to get the decorations for
         * @return a new ComponentDecorations instance containing the decorations of the child Component
         */
        public ComponentDecorations getChild(final Component component) {
            return new ComponentDecorations(new EnumMap<>(decorations), component);
        }

        /**
         * Gets the decorations of the Component.
         *
         * @return a map of TextDecoration to its state
         */
        public Map<TextDecoration, TextDecoration.State> getDecorations() {
            return decorations;
        }
    }

    /**
     * The ComponentBuilder class is used to build a Component of text by appending Components.
     */
    private static class ComponentBuilder {
        /**
         * The current Component being built.
         */
        @Nullable
        private Component current;

        /**
         * Creates a new ComponentBuilder instance.
         */
        public ComponentBuilder() {
        }

        /**
         * Appends a Component to the current component.
         *
         * @param component the Component to append
         */
        public void append(final Component component) {
            if (current == null) {
                current = component;
            } else {
                current = current.append(component);
            }
        }

        /**
         * Checks if the current component is empty.
         *
         * @return true if the current component is empty, false otherwise
         */
        public boolean isEmpty() {
            return current == null;
        }

        /**
         * Builds the current component and resets the builder.
         *
         * @return the built Component representing the current builder
         */
        public Component build() {
            if (current == null) {
                return Component.empty();
            }
            final Component component = current;
            current = null;
            return component;
        }
    }
}
