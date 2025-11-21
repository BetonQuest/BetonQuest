package org.betonquest.betonquest.compatibility.packetevents.conversation.display;

/**
 * A cursor that can be toggled on and off.
 * When disabled, it throws an exception when trying to get or set the current value.
 */
public class ToggleableCursor extends Cursor {
    /**
     * Whether the cursor is enabled or not.
     */
    private boolean enabled;

    /**
     * Creates a new ToggleableCursor.
     *
     * @param min     the minimum value of the cursor
     * @param max     the maximum value of the cursor
     * @param current the current value of the cursor
     * @param enabled whether the cursor is enabled or not
     */
    public ToggleableCursor(final int min, final int max, final int current, final boolean enabled) {
        super(min, max, current);
        this.enabled = enabled;
    }

    @Override
    public int get() {
        if (enabled) {
            return super.get();
        }
        throw new IllegalStateException("Cursor is not enabled");
    }

    @Override
    public void set(final int current) {
        if (enabled) {
            super.set(current);
        } else {
            throw new IllegalStateException("Cursor is not enabled");
        }
    }

    /**
     * Checks if the cursor is enabled.
     *
     * @return true if the cursor is enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether the cursor is enabled or not.
     *
     * @param enabled true to enable the cursor, false to disable it
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
}
