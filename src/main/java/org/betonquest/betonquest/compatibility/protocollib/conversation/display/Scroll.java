package org.betonquest.betonquest.compatibility.protocollib.conversation.display;

/**
 * Direction of a scroll in the menu io.
 */
public enum Scroll {
    @SuppressWarnings("PMD.ShortVariable")
    UP(-1),
    NONE(0),
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
