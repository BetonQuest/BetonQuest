package org.betonquest.betonquest.compatibility.packetevents.conversation.display;

import net.kyori.adventure.text.Component;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a line in a conversation display.
 * This interface allows for different types of lines to enable flexibility in how lines are displayed.
 */
@SuppressWarnings("PMD.ShortClassName")
@FunctionalInterface
public interface Line {
    /**
     * Gets the line to be displayed.
     *
     * @return the Component represented by this line
     */
    Component line();

    /**
     * A fixed line that does not change.
     *
     * @param line The line to be displayed.
     */
    record Fixed(Component line) implements Line {
        /**
         * Creates a new fixed line.
         *
         * @param line the Component to be displayed as a fixed line
         */
        public Fixed {
        }
    }

    /**
     * A swappable line that can change the content based on a condition.
     */
    class Swappable implements Line {
        /**
         * The line to be displayed when the condition is met.
         */
        private final Component selected;

        /**
         * The line to be displayed when the condition is not met.
         */
        private final Component unselected;

        /**
         * A supplier that determines whether the line is selected or not.
         */
        private final Supplier<Boolean> selector;

        /**
         * Creates a new swappable line.
         *
         * @param selected   the Component to be displayed when selected
         * @param unselected the Component to be displayed when not selected
         * @param selector   a supplier that determines if the line is selected (true) or not (false)
         */
        public Swappable(final Component selected, final Component unselected, final Supplier<Boolean> selector) {
            this.selected = selected;
            this.unselected = unselected;
            this.selector = selector;
        }

        @Override
        public Component line() {
            if (isSelected()) {
                return selected;
            } else {
                return unselected;
            }
        }

        /**
         * Checks if the line is selected based on the selector.
         *
         * @return true if the line is selected, false otherwise
         */
        public boolean isSelected() {
            return selector.get();
        }
    }

    /**
     * A toggleable line that is identified by a specific type.
     *
     * @param <T> the type of the identifier
     */
    class ToggleableIdentified<T> extends Swappable {
        /**
         * The identifier for this toggleable line.
         */
        private final T identifier;

        /**
         * Creates a new toggleable line with an identifier.
         *
         * @param selected   the Component to be displayed when selected
         * @param unselected the Component to be displayed when not selected
         * @param selector   a function that determines if the line is selected or not based on the identifier
         * @param identifier the identifier for this toggleable line
         */
        public ToggleableIdentified(final Component selected, final Component unselected, final Function<T, Boolean> selector, final T identifier) {
            super(selected, unselected, () -> selector.apply(identifier));
            this.identifier = identifier;
        }

        /**
         * Gets the identifier for this toggleable line.
         *
         * @return the identifier of this line
         */
        public T getIdentifier() {
            return identifier;
        }
    }
}
