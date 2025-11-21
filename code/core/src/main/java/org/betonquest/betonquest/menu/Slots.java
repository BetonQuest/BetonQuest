package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility object that handles which items are assigned to which slots.
 */
public class Slots {

    /**
     * RPG Menu instance to get ItemStacks.
     */
    private final RPGMenu rpgMenu;

    /**
     * First slot.
     */
    private final int start;

    /**
     * Last slot.
     */
    private final int end;

    /**
     * Items to set into the form.
     */
    private final Variable<List<MenuItemID>> items;

    /**
     * The slots form.
     */
    private final Type type;

    /**
     * .
     *
     * @param rpgMenu the rpg menu instance to get Menu Items
     * @param slots   the slot definition
     * @param items   the items to put at the slots
     * @throws IllegalArgumentException when the slot form is invalid or a number cannot be parsed
     */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public Slots(final RPGMenu rpgMenu, final String slots, final Variable<List<MenuItemID>> items) {
        this.rpgMenu = rpgMenu;
        if (slots.matches("\\d+")) {
            this.type = Type.SINGLE;
            this.start = Integer.parseInt(slots);
            this.end = start;
        } else if (slots.matches("\\d+-\\d+")) {
            this.type = Type.ROW;
            final int index = slots.indexOf('-');
            this.start = Integer.parseInt(slots.substring(0, index));
            this.end = Integer.parseInt(slots.substring(index + 1));
            if (this.end < this.start) {
                throw new IllegalArgumentException(slots + ": slot " + end + " must be after " + start);
            }
        } else if (slots.matches("\\d+\\*\\d+")) {
            this.type = Type.RECTANGLE;
            final int index = slots.indexOf('*');
            this.start = Integer.parseInt(slots.substring(0, index));
            this.end = Integer.parseInt(slots.substring(index + 1));
            if (this.end < this.start) {
                throw new IllegalArgumentException(slots + ": slot " + end + " must be after " + start);
            }
            if ((start % 9) > (end % 9)) {
                throw new IllegalArgumentException(slots + ": invalid rectangle ");
            }
        } else {
            throw new IllegalArgumentException(slots + " is not a valid slot identifier");
        }
        this.items = items;
    }

    /**
     * Checks if all defined slots are valid.
     *
     * @param slots         an iterable containing all slots objects to check
     * @param inventorySize the size of the inventory in which the slots should be
     * @throws QuestException if a defined list of slots is invalid
     */
    public static void checkSlots(final Iterable<Slots> slots, final int inventorySize) throws QuestException {
        final boolean[] contained = new boolean[inventorySize]; //initialized with 'false'
        for (final Slots s : slots) {
            for (final int slot : s.getSlots()) {
                try {
                    if (contained[slot]) {
                        throw new QuestException("Slots '" + s + "': slot " + slot + " was already specified");
                    } else {
                        contained[slot] = true;
                    }
                } catch (final IndexOutOfBoundsException e) {
                    throw new QuestException("Slots '" + s + "': slot " + slot + " exceeds inventory size", e);
                }
            }
        }
    }

    /**
     * Get the ids of used inventory slots.
     *
     * @return a sorted list of all slots which are covered by this slots object
     */
    public List<Integer> getSlots() {
        final List<Integer> slots = new ArrayList<>();
        switch (type) {
            case SINGLE:
                slots.add(start);
                break;
            case ROW:
                for (int i = start; i <= end; i++) {
                    slots.add(i);
                }
                break;
            case RECTANGLE:
                int index = start;
                while (index <= end) {
                    slots.add(index);
                    //set i to next slot of rectangle
                    if ((index % 9) < (end % 9)) {
                        index++;
                    } else {
                        index += 8 - (index % 9) + (start % 9) + 1;
                    }
                }
                break;
        }
        return slots;
    }

    /**
     * Checks if the slot id is part of this.
     *
     * @param slot the slot to check for
     * @return if this slots object covers the given slot
     */
    public boolean containsSlot(final int slot) {
        return switch (type) {
            case SINGLE -> start == slot;
            case ROW -> slot <= end && slot >= start;
            case RECTANGLE -> slot <= end
                    && slot >= start
                    && slot % 9 >= start % 9
                    && slot % 9 <= end % 9;
        };
    }

    /**
     * Get the actual displayed items.
     *
     * @param profile the player from the {@link Profile} for which these slots should get displayed for
     * @return all items which should be shown to the specified player of the slots covered by this object
     * @throws QuestException if an error occurs while resolving the items
     */
    public List<MenuItem> getItems(final Profile profile) throws QuestException {
        final List<MenuItem> items = new ArrayList<>();
        for (final MenuItemID itemID : this.items.getValue(profile)) {
            final MenuItem item = rpgMenu.getMenuItem(itemID);
            if (item.display(profile)) {
                items.add(item);
            }
        }
        return items;
    }

    /**
     * Get the inventory's index as relative index within this.
     *
     * @param slot the index of the slot in the menu
     * @return the index of the given slot within this collection of slots, -1 if slot is not within this collection
     */
    public int getIndex(final int slot) {
        if (!containsSlot(slot)) {
            return -1;
        }
        return switch (type) {
            case SINGLE:
                yield 0;
            case ROW:
                yield slot - start;
            case RECTANGLE:
                final int rectangleLength = end % 9 - start % 9 + 1;
                final int rows = slot / 9 - start / 9;
                yield rectangleLength * rows + slot % 9 - start % 9;
        };
    }

    /**
     * Get the item from the inventory's index.
     *
     * @param profile the player {@link Profile} for which these slots should get displayed for
     * @param slot    the slot which should contain this item
     * @return the menu item which should be displayed in the given slot to the player
     * @throws QuestException if an error occurs while resolving the items
     */
    @Nullable
    public MenuItem getItem(final Profile profile, final int slot) throws QuestException {
        final int index = this.getIndex(slot);
        if (index == -1) {
            throw new IllegalStateException("Invalid slot for Slots '" + this + "': " + slot);
        }
        try {
            return this.getItems(profile).get(index);
        } catch (final IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Get the slot type.
     *
     * @return the type of this slots object
     */
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return switch (type) {
            case SINGLE -> String.valueOf(start);
            case ROW -> start + "-" + end;
            case RECTANGLE -> start + "*" + end;
        };
    }

    /**
     * The form of slot assignment.
     */
    public enum Type {
        /**
         * A single slot.
         */
        SINGLE,

        /**
         * Multiple slots ordered in a row, one behind each other.
         */
        ROW,

        /**
         * Multiple slots ordered in a rectangle.
         */
        RECTANGLE
    }
}
