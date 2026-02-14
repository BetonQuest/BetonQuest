package org.betonquest.betonquest.conversation;

/**
 * Types of conversation options.
 */
public enum ConversationOptionType {

    /**
     * Things the NPC says.
     */
    NPC("NPC_options", "NPC option"),

    /**
     * Options selectable by the player.
     */
    PLAYER("player_options", "player option");

    /**
     * The section name.
     */
    private final String identifier;

    /**
     * The name to use in logging.
     */
    private final String readable;

    ConversationOptionType(final String identifier, final String readable) {
        this.identifier = identifier;
        this.readable = readable;
    }

    /**
     * Get the section name.
     *
     * @return section identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Gets the readable type name.
     *
     * @return name to use in log messages
     */
    public String getReadable() {
        return readable;
    }
}
