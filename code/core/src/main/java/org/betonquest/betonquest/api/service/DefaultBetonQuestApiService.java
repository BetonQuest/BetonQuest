package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.BetonQuestApiService;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;

import java.util.function.Supplier;

/**
 * The default implementation of the {@link BetonQuestApiService}.
 */
public class DefaultBetonQuestApiService implements BetonQuestApiService {

    /**
     * The {@link ProfileProvider} supplier to retrieve the {@link ProfileProvider} instance.
     */
    private final Supplier<ProfileProvider> profileProvider;

    /**
     * The {@link QuestPackageManager} supplier to retrieve the {@link QuestPackageManager} instance.
     */
    private final Supplier<QuestPackageManager> packageManager;

    /**
     * The {@link BetonQuestLoggerFactory} supplier to retrieve the {@link BetonQuestLoggerFactory} instance.
     */
    private final Supplier<BetonQuestLoggerFactory> loggerFactory;

    /**
     * The {@link BetonQuestInstructions} supplier to retrieve the {@link BetonQuestInstructions} instance.
     */
    private final Supplier<BetonQuestInstructions> instructions;

    /**
     * The {@link BetonQuestConversations} supplier to retrieve the {@link BetonQuestConversations} instance.
     */
    private final Supplier<BetonQuestConversations> conversations;

    /**
     * The {@link BetonQuestRegistries} supplier to retrieve the {@link BetonQuestRegistries} instance.
     */
    private final Supplier<BetonQuestRegistries> registries;

    /**
     * The {@link BetonQuestManagers} supplier to retrieve the {@link BetonQuestManagers} instance.
     */
    private final Supplier<BetonQuestManagers> managers;

    /**
     * Creates a new instance of the {@link DefaultBetonQuestApiService}.
     *
     * @param profileProvider the {@link ProfileProvider} supplier
     *                        to retrieve the {@link ProfileProvider} instance.
     * @param packageManager  the {@link QuestPackageManager} supplier
     *                        to retrieve the {@link QuestPackageManager} instance.
     * @param loggerFactory   the {@link BetonQuestLoggerFactory} supplier
     *                        to retrieve the {@link BetonQuestLoggerFactory} instance.
     * @param instructions    the {@link BetonQuestInstructions} supplier
     *                        to retrieve the {@link BetonQuestInstructions} instance.
     * @param conversations   the {@link BetonQuestConversations} supplier
     *                        to retrieve the {@link BetonQuestConversations} instance.
     * @param registries      the {@link BetonQuestRegistries} supplier
     *                        to retrieve the {@link BetonQuestRegistries} instance.
     * @param managers        the {@link BetonQuestManagers} supplier
     *                        to retrieve the {@link BetonQuestManagers} instance.
     */
    public DefaultBetonQuestApiService(final Supplier<ProfileProvider> profileProvider, final Supplier<QuestPackageManager> packageManager,
                                       final Supplier<BetonQuestLoggerFactory> loggerFactory, final Supplier<BetonQuestInstructions> instructions,
                                       final Supplier<BetonQuestConversations> conversations, final Supplier<BetonQuestRegistries> registries,
                                       final Supplier<BetonQuestManagers> managers) {
        this.profileProvider = profileProvider;
        this.packageManager = packageManager;
        this.loggerFactory = loggerFactory;
        this.instructions = instructions;
        this.conversations = conversations;
        this.registries = registries;
        this.managers = managers;
    }

    @Override
    public ProfileProvider getProfiles() {
        return profileProvider.get();
    }

    @Override
    public QuestPackageManager getPackages() {
        return packageManager.get();
    }

    @Override
    public BetonQuestLoggerFactory getLoggers() {
        return loggerFactory.get();
    }

    @Override
    public BetonQuestInstructions getInstructions() {
        return instructions.get();
    }

    @Override
    public BetonQuestConversations getConversations() {
        return conversations.get();
    }

    @Override
    public BetonQuestRegistries getRegistries() {
        return registries.get();
    }

    @Override
    public BetonQuestManagers getManagers() {
        return managers.get();
    }
}
