package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.BetonQuestApiInstance;
import org.betonquest.betonquest.api.BetonQuestApiServices;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;

import java.util.function.Supplier;

/**
 * The default implementation of the {@link BetonQuestApiServices}.
 */
public class DefaultBetonQuestApi implements BetonQuestApiInstance {

    /**
     * The {@link ProfileProvider} supplier.
     */
    private final Supplier<ProfileProvider> profileProvider;

    /**
     * The {@link QuestPackageManager} supplier.
     */
    private final Supplier<QuestPackageManager> packageManager;

    /**
     * The {@link BetonQuestLoggerFactory} supplier.
     */
    private final Supplier<BetonQuestLoggerFactory> loggerFactory;

    /**
     * The {@link BetonQuestInstructions} supplier.
     */
    private final Supplier<BetonQuestInstructions> instructions;

    /**
     * The {@link BetonQuestConversations} supplier.
     */
    private final Supplier<BetonQuestConversations> conversations;

    /**
     * The {@link BetonQuestRegistries} supplier.
     */
    private final Supplier<BetonQuestRegistries> registries;

    /**
     * The {@link BetonQuestManagers} supplier.
     */
    private final Supplier<BetonQuestManagers> managers;

    /**
     * Creates a new instance of the {@link DefaultBetonQuestApi}.
     *
     * @param profileProvider the {@link ProfileProvider} supplier
     * @param packageManager  the {@link QuestPackageManager} supplier
     * @param loggerFactory   the {@link BetonQuestLoggerFactory} supplier
     * @param instructions    the {@link BetonQuestInstructions} supplier
     * @param conversations   the {@link BetonQuestConversations} supplier
     * @param registries      the {@link BetonQuestRegistries} supplier
     * @param managers        the {@link BetonQuestManagers} supplier
     */
    public DefaultBetonQuestApi(final Supplier<ProfileProvider> profileProvider, final Supplier<QuestPackageManager> packageManager,
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
