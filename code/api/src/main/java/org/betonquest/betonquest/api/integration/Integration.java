package org.betonquest.betonquest.api.integration;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;

/**
 * This interface defines the lifecycle methods for integrations with external plugins.
 * Implementations should register their custom conditions, actions, objectives, placeholders, and other
 * BetonQuest features during the {@link #enable(BetonQuestApi)} phase.
 * <p>
 * The integration lifecycle consists of three phases:
 * <ul>
 *     <li>{@link #enable(BetonQuestApi)}
 *          - Called when the integration is enabled during BetonQuests onEnable method</li>
 *     <li>{@link #postEnable(BetonQuestApi)}
 *          - Called after all plugins are enabled but before server ticking starts</li>
 *     <li>{@link #disable()}
 *          - Called when the integration should clean up its resources when BetonQuest is disabling</li>
 * </ul>
 * <p>
 * Integrations are typically registered through an {@link IntegrationService} which handles version
 * compatibility checks and ensures integrations are only enabled when their dependencies are available.
 *
 * @see IntegrationService
 */
public interface Integration {

    /**
     * Enables the integration during BetonQuest's plugin onEnable phase.
     * <p>
     * This method is called when the integration is attempted to be activated and is the primary place to register
     * custom BetonQuest features such as conditions, actions, objectives, placeholders, and other features.
     * Implementations should use the provided {@link BetonQuestApi} to register their features with BetonQuest.
     * <p>
     * This method is invoked during BetonQuest's onEnable lifecycle, which means:
     * <ul>
     *     <li>BetonQuest's core systems are initialized and ready to accept registrations</li>
     *     <li>Both the integrated and integrating plugins are enabled</li>
     *     <li>Other plugins may not yet be fully enabled</li>
     *     <li>The server is not yet ticking</li>
     * </ul>
     * <p>
     * If your integration requires functionality that depends on all plugins being enabled or the server
     * having started ticking, use {@link #postEnable(BetonQuestApi)} instead since registration
     * will no longer be available after BetonQuest has enabled its integrations.
     *
     * @param betonQuestApi the {@link BetonQuestApi} instance used to register integration features created for the
     *                      plugin that has registered this integration
     * @throws QuestException if the integration fails to enable due to user-defined errors
     * @see #postEnable(BetonQuestApi)
     * @see IntegrationService
     */
    void enable(BetonQuestApi betonQuestApi) throws QuestException;

    /**
     * Enables all parts of the integration that are required to load after all plugins have been enabled, but
     * before the server has started ticking.
     * <p>
     * It provides a safe point to perform initialization tasks that require other plugins
     * to be fully enabled and available.
     * <p>
     * This method is invoked during BetonQuest's post-enable lifecycle, which means:
     * <ol>
     *     <li>All plugins on the server have completed their onEnable phase</li>
     *     <li>BetonQuest has finished registering all integrations as well as enabling them</li>
     *     <li>Other plugin APIs and services are fully initialized and accessible</li>
     *     <li>The server has not yet started its main tick loop</li>
     *     <li>Registration of new BetonQuest features (conditions, actions, objectives, etc.) is <i>still</i> possible</li>
     *     <li>Registration of new integrations is <i>no longer</i> possible</li>
     * </ol>
     * <p>
     * Use this method for:
     * <ul>
     *     <li>Establishing connections or references to other plugins that must be enabled first</li>
     *     <li>Performing validation checks that require other plugins' data to be available</li>
     *     <li>Setting up listeners or hooks that depend on multiple plugins being ready</li>
     *     <li>Initializing resources that require cross-plugin coordination</li>
     * </ul>
     * <p>
     * If your integration only needs to register BetonQuest features and does not have dependencies on other
     * plugins being enabled, use {@link #enable(BetonQuestApi)} instead, as this method may not be necessary.
     *
     * @param betonQuestApi the {@link BetonQuestApi} instance providing access to BetonQuest's API
     * @throws QuestException if the post-enable phase fails due to user-defined errors
     * @see #enable(BetonQuestApi)
     * @see IntegrationService
     */
    void postEnable(BetonQuestApi betonQuestApi) throws QuestException;

    /**
     * Disables the integration when BetonQuest is shutting down.
     * <p>
     * This method is called during BetonQuest's plugin onDisable phase and should be used to clean up any resources,
     * close connections, or perform other teardown tasks that are necessary for a graceful shutdown of the integration.
     * <p>
     * This method is only invoked during BetonQuest's onDisable lifecycle, which means:
     * <ul>
     *     <li>The server is shutting down or the plugin is being disabled</li>
     *     <li>Other plugins may already be disabled or in the process of disabling</li>
     *     <li>External resources that are not handed to BetonQuest via the API should be released</li>
     *     <li>Accessing features in the API will most likely cause errors, though logging should work fine</li>
     * </ul>
     *
     * @throws QuestException if the integration teardown fails due to user-defined errors
     * @see #enable(BetonQuestApi)
     * @see #postEnable(BetonQuestApi)
     */
    void disable() throws QuestException;
}
