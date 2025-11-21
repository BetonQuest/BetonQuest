package org.betonquest.betonquest.compatibility.packetevents.conversation.display;

/**
 * A cursor that can be used to navigate through a range of values.
 * It maintains a minimum and maximum value, and allows for modification of the current value
 * while ensuring it stays within the defined range.
 */
public class Cursor {
    /**
     * The minimum value of the cursor.
     */
    private final int min;

    /**
     * The maximum value of the cursor.
     */
    private final int max;

    /**
     * The current value of the cursor.
     */
    private int current;

    /**
     * Creates a new cursor with the specified minimum, maximum, and current values.
     *
     * @param min     the minimum value of the cursor
     * @param max     the maximum value of the cursor
     * @param current the current value of the cursor
     */
    public Cursor(final int min, final int max, final int current) {
        this.min = min;
        this.max = max;
        this.current = Math.max(min, Math.min(max, current));
    }

    /**
     * Gets the current value of the cursor.
     *
     * @return the current value of the cursor, which is guaranteed to be within the range of min and max
     */
    public int get() {
        return current;
    }

    /**
     * Sets the current value of the cursor, ensuring it stays within the defined range.
     *
     * @param current the new current value to set for the cursor
     */
    public void set(final int current) {
        this.current = Math.min(max, Math.max(min, current));
    }

    /**
     * Modifies the current value of the cursor by a given modification value.
     * This is done by calling {@link #set(int)} with the current value plus the modification.
     *
     * @param modification the value to modify the current cursor position by
     */
    public void modify(final int modification) {
        set(current + modification);
    }

    /**
     * Checks if the given value is valid for this cursor.
     *
     * @param value the value to check
     * @return true if the value is within the range of the cursor, false otherwise
     */
    public boolean isValid(final int value) {
        return value >= min && value <= max;
    }
}
