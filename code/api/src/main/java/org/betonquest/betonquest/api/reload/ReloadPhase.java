package org.betonquest.betonquest.api.reload;

/**
 * Each reload phase represents a state in the reloading process.
 * Each phase is executed iteratively after each other, therefore, every following phase combines newly reloaded
 * reloadables with everything already reloaded in the previous phase.
 */
public enum ReloadPhase {

    /**
     * Earliest phase. Right after the reload is triggered and nothing is reloaded yet.
     */
    PRE_RELOAD,
    /**
     * Second phase. The configuration is reloaded.
     */
    CONFIG,
    /**
     * Third phase. All files in quest packages are reloaded.
     */
    PACKAGES,
    /**
     * Fourth phase. All integrations are reloaded.
     */
    INTEGRATION,
    /**
     * Fifth phase. All instructions have been reloaded, parsed, and instantiated.
     */
    INSTANCING,
    /**
     * Sixth phase. The profiles are reloaded.
     */
    PROFILES,
    /**
     * Last phase. All internal logic is reloaded.
     */
    POST_RELOAD
}
