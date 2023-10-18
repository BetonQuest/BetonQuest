package org.betonquest.betonquest.commands.quest.download;

public enum RepositoryLayoutRule {
    FORCE_STRUCTURED,
    FORCE_RAW,
    AUTO_DETECT,
    ;

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
