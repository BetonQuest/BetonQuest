package org.betonquest.betonquest.compatibility.packetevents.conversation.display;

/**
 * Direction of a scroll in the menu io.
 */
public enum Scroll {
    /**
     * Scroll up by decrementing the value by 1.
     */
    @SuppressWarnings("PMD.ShortVariable")
    UP(-1),
    /**
     * No scroll, the value remains unchanged.
     */
    NONE(0),
    /**
     * Scroll down by incrementing the value by 1.
     */
    DOWN(1);

    /**
     * The modification value of the scroll.
     */
    private final int modification;

    Scroll(final int modification) {
        this.modification = modification;
    }

    /**
     * Gets the modification value of the scroll.
     *
     * @return the modification value
     */
    public int getModification() {
        return modification;
    }
}
