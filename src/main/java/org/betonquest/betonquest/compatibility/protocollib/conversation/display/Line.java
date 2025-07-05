package org.betonquest.betonquest.compatibility.protocollib.conversation.display;

import net.kyori.adventure.text.Component;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a line in a conversation display.
 * This interface allows for different types of lines, to enable flexibility in how lines are displayed.
 */
@SuppressWarnings("PMD.ShortClassName")
@FunctionalInterface
public interface Line {
    /**
     * Gets the line to be displayed.
     *
     * @return the Component representing the line
     */
    Component getLine();

    /**
     * A fixed line that does not change.
     */
    class Fixed implements Line {
        /**
         * The line to be displayed.
         */
        private final Component line;

        /**
         * Creates a new fixed line.
         *
         * @param line the Component to be displayed as a fixed line
         */
        public Fixed(final Component line) {
            this.line = line;
        }

        @Override
        public Component getLine() {
            return line;
        }
    }

    /**
     * A toggleable line that can change based on a condition.
     */
    class Toggleable implements Line {
        /**
         * The line to be displayed when the condition is not met.
         */
        private final Component unselected;

        /**
         * The line to be displayed when the condition is met.
         */
        private final Component selected;

        /**
         * A supplier that determines whether the line is selected or not.
         */
        private final Supplier<Boolean> selector;

        /**
         * Creates a new toggleable line.
         *
         * @param unselected the Component to be displayed when not selected
         * @param selected   the Component to be displayed when selected
         * @param selector   a supplier that returns true if the line is selected, false otherwise
         */
        public Toggleable(final Component unselected, final Component selected, final Supplier<Boolean> selector) {
            this.unselected = unselected;
            this.selected = selected;
            this.selector = selector;
        }

        @Override
        public Component getLine() {
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
    class ToggleableIdentified<T> extends Toggleable {
        /**
         * The identifier for this toggleable line.
         */
        private final T identifier;

        /**
         * Creates a new toggleable line with an identifier.
         *
         * @param unselected the Component to be displayed when not selected
         * @param selected   the Component to be displayed when selected
         * @param selector   a function that returns true if the line is selected for the given identifier, false otherwise
         * @param identifier the identifier for this toggleable line
         */
        public ToggleableIdentified(final Component unselected, final Component selected, final Function<T, Boolean> selector, final T identifier) {
            super(unselected, selected, () -> selector.apply(identifier));
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
