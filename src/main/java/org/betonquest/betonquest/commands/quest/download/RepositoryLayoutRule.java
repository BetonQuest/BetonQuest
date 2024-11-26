package org.betonquest.betonquest.commands.quest.download;

/**
 * Rule to determine the repository layout of a remote repository to be downloaded.
 */
public enum RepositoryLayoutRule {
    /**
     * Force the repository layout to be structured. That is, to expect at least a QuestPackages or QuestTemplates
     * folder or both in the selected source directory.
     */
    FORCE_STRUCTURED,

    /**
     * Force the repository layout to be raw. That is, no structure is expected to define if something is a package or
     * template and thus this information needs to be provided via another way.
     */
    FORCE_RAW,

    /**
     * Detect automatically if the repository is structured by looking for folders with the name QuestPackages or
     * QuestTemplates. This rule might require additional validation after the repository was successfully downloaded.
     */
    AUTO_DETECT;

    /**
     * Get the layout rule from two boolean flags. Each flag forces one mode and falling back to auto-detection if none
     * are set. Forcing more than one mode fails as only one mode can be forced at a time.
     *
     * @param forceStructured if structured layout should be forced
     * @param forceRaw        if raw layout should be forced
     * @return the layout rule to apply
     * @throws IllegalArgumentException if more than one mode is being forced
     */
    public static RepositoryLayoutRule fromFlags(final boolean forceStructured, final boolean forceRaw) {
        if (forceStructured) {
            if (forceRaw) {
                throw new IllegalArgumentException("Cannot force both raw and structured layout, either one must be chosen!");
            } else {
                return FORCE_STRUCTURED;
            }
        } else {
            if (forceRaw) {
                return FORCE_RAW;
            } else {
                return AUTO_DETECT;
            }
        }
    }
}
