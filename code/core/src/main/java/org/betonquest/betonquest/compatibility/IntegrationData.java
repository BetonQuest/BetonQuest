package org.betonquest.betonquest.compatibility;

/**
 * Data of hooking into Plugins or Minecraft versions.
 */
public interface IntegrationData {

    /**
     * Gets if the integration successfully hooked.
     *
     * @return if the integration was successful
     */
    boolean isIntegrated();

    /**
     * Gets the target name to display.
     *
     * @return the name of the hooked
     * @throws IllegalStateException when not {@link #isIntegrated()}
     */
    String getName();

    /**
     * Gets the version to show in the compatibility.
     *
     * @return the version string of the hooked
     * @throws IllegalStateException when not {@link #isIntegrated()}
     */
    String getVersion();
}
