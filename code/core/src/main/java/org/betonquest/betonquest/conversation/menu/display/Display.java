package org.betonquest.betonquest.conversation.menu.display;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.betonquest.betonquest.api.common.component.FixedComponentLineWrapper;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.conversation.menu.MenuConvIOSettings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A display for a conversations displayed with components.
 * It contains the NPC name, NPC text, and options to be displayed.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.TooManyMethods"})
public class Display {

    /**
     * The screen that displays the conversation.
     */
    private final LineView screen;

    /**
     * A cursor that can be used to scroll through the conversation display.
     */
    private final Cursor scroll;

    /**
     * A toggleable cursor that can be used to select options.
     */
    private final ToggleableCursor select;

    /**
     * A set of identifiers for the previous viewable options.
     */
    private final Set<Integer> lastViewableOptions;

    /**
     * Creates a new display for the conversation.
     *
     * @param settings the settings for the menu conversation IO
     * @param wrapper  the wrapper for fixed component lines
     * @param npcName  the name of the NPC to be displayed
     * @param npcText  the text of the NPC to be displayed
     * @param options  the options to be displayed to choose from
     */
    public Display(final MenuConvIOSettings settings, final FixedComponentLineWrapper wrapper, final Component npcName,
                   final Component npcText, final List<Component> options) {
        this.select = new ToggleableCursor(0, options.size() - 1, 0, false);
        final Line scrollUp = new Line.Fixed(settings.scrollUp());
        final Line scrollDown = new Line.Fixed(settings.scrollDown());

        final boolean npcNameChat = "chat".equalsIgnoreCase(settings.npcNameType());
        int lineCount = settings.lineCount();
        if (npcNameChat) {
            lineCount--;
        }

        final LineView npcNameView = getFormattedNpcName(settings, wrapper, npcName, npcNameChat);
        final LineView npcTextView = getFormattedNpcLines(settings, wrapper, npcText, npcNameChat);
        final LineView optionView = getFormattedOptionLines(settings, wrapper, options);

        final int minimalHeight = npcTextView.getSize() + 1;
        final int fillHeight = settings.optionsSeparator() ? lineCount - optionView.getSize() : 0;
        final LineView npcTextFilledView = new LineView.Filler(npcTextView, Math.max(minimalHeight, fillHeight));

        final LineView combined = new LineView.Combiner(npcTextFilledView, optionView);
        this.scroll = new Cursor(0, combined.getSize() - lineCount, 0);
        final LineView excerpt = new LineView.Excerpt(lineCount, scroll::get, combined, scrollUp, scrollDown);
        final LineView filler = new LineView.Filler(new LineView.Empty(), settings.lineFillBefore());
        this.screen = new LineView.Combiner(filler, new LineView.Combiner(npcNameView, excerpt));
        this.lastViewableOptions = new HashSet<>();
    }

    private LineView getFormattedNpcName(final MenuConvIOSettings settings, final FixedComponentLineWrapper wrapper,
                                         final Component npcName, final boolean npcNameChat) {
        if (npcNameChat) {
            final Component name = settings.npcName().resolve(new VariableReplacement("npc_name", npcName));
            return new LineView.Holder(new Line.Fixed(switch (settings.npcNameAlign()) {
                case "right" -> Component.text(" ".repeat(getRemainingSpace(wrapper, name))).append(name);
                case "center" -> Component.text(" ".repeat(getRemainingSpace(wrapper, name) / 2)).append(name);
                case "left" -> name;
                default -> name;
            }));
        }
        return new LineView.Empty();
    }

    private int getRemainingSpace(final FixedComponentLineWrapper wrapper, final Component component) {
        return Math.max(0, wrapper.getMaxLineWidth() - wrapper.width(component)) / wrapper.width(Component.text(" "));
    }

    private LineView getFormattedNpcLines(final MenuConvIOSettings settings, final FixedComponentLineWrapper wrapper,
                                          final Component npcText, final boolean npcNameChat) {
        final List<Line> lines = new ArrayList<>();
        if (npcNameChat && settings.npcNameSeparator()) {
            lines.add(new Line.Fixed(Component.empty()));
        }
        final VariableReplacement replacement = new VariableReplacement("npc_text", npcText);
        wrapper.splitWidth(settings.npcText().resolve(replacement), getPrefixComponentSupplier(settings.npcTextWrap())).stream()
                .map(Line.Fixed::new)
                .forEach(lines::add);
        return new LineView.Holder(lines);
    }

    private LineView getFormattedOptionLines(final MenuConvIOSettings settings,
                                             final FixedComponentLineWrapper wrapper,
                                             final List<Component> options) {
        final List<Line> optionLines = new ArrayList<>();
        int optionCount = 0;
        for (final Component option : options) {
            final VariableReplacement replacement = new VariableReplacement("option_text", option);
            final List<Component> optionUnselected = wrapper.splitWidth(settings.optionText().resolve(replacement),
                    getPrefixComponentSupplier(settings.optionTextWrap()));
            final List<Component> optionSelected = wrapper.splitWidth(settings.optionSelectedText().resolve(replacement),
                    getPrefixComponentSupplier(settings.optionSelectedTextWrap()));

            for (int i = 0; i < Math.max(optionUnselected.size(), optionSelected.size()); i++) {
                final Component selected = i < optionSelected.size() ? optionSelected.get(i) : Component.empty();
                final Component unselected = i < optionUnselected.size() ? optionUnselected.get(i) : Component.empty();
                optionLines.add(new ToggleableIndexLine(selected, unselected, (index) -> select.isEnabled() && select.get() == index, optionCount));
            }
            optionCount++;
        }
        return new LineView.Holder(optionLines);
    }

    private Supplier<Component> getPrefixComponentSupplier(final Component component) {
        final AtomicBoolean first = new AtomicBoolean(true);
        return () -> {
            if (first.get()) {
                first.set(false);
                return Component.empty();
            }
            return component;
        };
    }

    /**
     * Get the current selection state of the display.
     *
     * @return the current selection as an Optional Integer or empty if no selection is made.
     */
    public Optional<Integer> getSelection() {
        if (select.isEnabled()) {
            return Optional.of(select.get());
        }
        return Optional.empty();
    }

    /**
     * Get the current screen for the given scroll state.
     *
     * @param scroll The scroll state modification, -1, 0 or +1
     * @return the selected screen as a component.
     */
    public Component getDisplay(final Scroll scroll) {
        if (scroll != Scroll.NONE) {
            checkNewScroll(scroll);
        }
        final List<Line> lines = screen.lines();
        setupFirstScreen(scroll, lines);
        if (scroll != Scroll.NONE) {
            checkNewSelect(scroll, lines);
        }
        final List<Component> displayLines = lines.stream().map(Line::line).toList();
        return Component.join(JoinConfiguration.newlines(), displayLines);
    }

    private void setupFirstScreen(final Scroll scroll, final List<Line> lines) {
        if (scroll != Scroll.NONE || this.scroll.get() != 0 || this.select.isEnabled()) {
            return;
        }
        final List<ToggleableIndexLine> options = lines.stream()
                .filter(line -> line instanceof ToggleableIndexLine)
                .map(line -> (ToggleableIndexLine) line)
                .toList();
        options.forEach(option -> lastViewableOptions.add(option.getIdentifier()));
        if (!options.isEmpty() && !this.select.isValid(options.get(options.size() - 1).getIdentifier() + 1)) {
            this.select.setEnabled(true);
        }
    }

    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    private void checkNewScroll(final Scroll scroll) {
        if (select.isEnabled()) {
            final int selectOffset = select.get() + (scroll.getModification() * 2);
            if (select.isValid(selectOffset)) {
                if (lastViewableOptions.contains(selectOffset)) {
                    return;
                }
            } else if (select.get() != 0) {
                return;
            }
        }
        this.scroll.modify(scroll.getModification());
    }

    private void checkNewSelect(final Scroll scroll, final List<Line> lines) {
        final Set<Integer> previousLastViewableOptions = new HashSet<>(this.lastViewableOptions);
        this.lastViewableOptions.clear();
        final List<ToggleableIndexLine> options = lines.stream()
                .filter(line -> line instanceof ToggleableIndexLine)
                .map(line -> (ToggleableIndexLine) line)
                .toList();
        if (options.isEmpty()) {
            select.setEnabled(false);
            return;
        }
        options.forEach(line -> lastViewableOptions.add(line.getIdentifier()));
        if (previousLastViewableOptions.isEmpty()) {
            return;
        }
        if (!select.isEnabled()) {
            select.setEnabled(true);
            return;
        }

        checkNewSelectFromIndex(scroll, options, previousLastViewableOptions);
    }

    private void checkNewSelectFromIndex(final Scroll scroll, final List<ToggleableIndexLine> options,
                                         final Set<Integer> previousLastViewableOptions) {
        Integer selectedIndex = null;
        for (int i = 0; i < options.size(); i++) {
            final ToggleableIndexLine optionLine = options.get(i);
            if (optionLine.isSelected()) {
                selectedIndex = i;
                break;
            }
        }
        if (selectedIndex == null) {
            final int borderIndex = scroll == Scroll.UP ? options.size() - 1 : 0;
            this.select.set(options.get(borderIndex).getIdentifier());
            return;
        }
        checkNewSelectFromIndexOption(scroll, options, selectedIndex, previousLastViewableOptions);
    }

    private void checkNewSelectFromIndexOption(final Scroll scroll, final List<ToggleableIndexLine> options, final int index,
                                               final Set<Integer> previousLastViewableOptions) {
        final int selectedIndex = index + scroll.getModification();
        if (selectedIndex >= 0 && options.size() > selectedIndex) {
            final Integer select = options.get(selectedIndex).getIdentifier();
            if (this.select.get() == select) {
                checkNewSelectFromIndexOption(scroll, options, selectedIndex, previousLastViewableOptions);
            } else {
                final boolean borderIndex = scroll == Scroll.UP && selectedIndex == 0
                        || scroll == Scroll.DOWN && selectedIndex == options.size() - 1;
                if (!borderIndex || !this.select.isValid(select + scroll.getModification()) && previousLastViewableOptions.contains(select)) {
                    this.select.set(select);
                }
            }
        }
    }

    /**
     * A toggleable index line that can change based on a condition.
     */
    private static class ToggleableIndexLine extends Line.ToggleableIdentified<Integer> {

        /**
         * Creates a new toggleable index line.
         *
         * @param selected   the Component to be displayed when selected
         * @param unselected the Component to be displayed when not selected
         * @param selector   a function that determines if the line is selected or not based on the identifier
         * @param identifier the identifier for this toggleable line
         */
        public ToggleableIndexLine(final Component selected, final Component unselected, final Function<Integer, Boolean> selector, final Integer identifier) {
            super(selected, unselected, selector, identifier);
        }
    }
}
