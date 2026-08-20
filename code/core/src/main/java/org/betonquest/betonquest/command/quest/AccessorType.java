package org.betonquest.betonquest.command.quest;

/**
 * Accessor Type for ID completion.
 * The enum in lower case is the used section.
 */
public enum AccessorType {
    /**
     * ActionID.
     */
    ACTIONS(true, true),
    /**
     * ConditionID.
     */
    CONDITIONS(true, true),
    /**
     * ObjectiveID.
     */
    OBJECTIVES(true),
    /**
     * ItemID.
     */
    ITEMS(true),
    /**
     * JournalID.
     */
    JOURNAL(false);

    /**
     * If the accessor allows nested ids.
     */
    public final boolean allowNested;

    /**
     * If section nodes also are identifiers.
     */
    public final boolean allowSection;

    AccessorType(final boolean allowNested) {
        this(allowNested, false);
    }

    AccessorType(final boolean allowNested, final boolean allowSection) {
        this.allowNested = allowNested;
        this.allowSection = allowSection;
    }
}
