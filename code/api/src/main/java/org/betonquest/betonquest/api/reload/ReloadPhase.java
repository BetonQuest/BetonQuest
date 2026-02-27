package org.betonquest.betonquest.api.reload;

/**
 * Each reload phase represents a state in the reloading process.
 * Each phase is executed iteratively after each other, therefore, every following phase combines newly reloaded
 * reloadables with everything already reloaded in the previous phase.
 */
public enum ReloadPhase {

    /**
     * First phase. The configuration will reload.
     */
    CONFIG,
    /**
     * Second phase. All files in quest packages will reload.
     */
    PACKAGES,
    /**
     * Third phase. All integrations will reload.
     */
    INTEGRATION,
    /**
     * Fourth phase. All instructions will reload, parse, and instantiate.
     */
    INSTANCING,
    /**
     * Fifth phase. The profiles will reload.
     */
    PROFILES,
    /**
     * Last phase. All remaining internal logic will reload.
     */
    POST
}
