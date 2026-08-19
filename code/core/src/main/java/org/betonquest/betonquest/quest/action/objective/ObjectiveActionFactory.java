package org.betonquest.betonquest.quest.action.objective;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.Saver;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Locale;

/**
 * Factory for {@link ObjectiveAction}s.
 */
public class ObjectiveActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * The BetonQuest instance.
     */
    private final Plugin plugin;

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The objective manager.
     */
    private final ObjectiveManager objectiveManager;

    /**
     * The player data storage.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * The database saver.
     */
    private final Saver saver;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Creates a new factory for {@link ObjectiveAction}s.
     *
     * @param plugin            the plugin instance
     * @param loggerFactory     the logger factory to create a logger for the actions
     * @param profileProvider   the profile provider instance
     * @param saver             the database saver
     * @param objectiveManager  the objective manager
     * @param playerDataStorage the player data storage
     */
    public ObjectiveActionFactory(final Plugin plugin, final BetonQuestLoggerFactory loggerFactory,
                                  final ProfileProvider profileProvider, final Saver saver, final ObjectiveManager objectiveManager,
                                  final PlayerDataStorage playerDataStorage) {
        this.plugin = plugin;
        this.profileProvider = profileProvider;
        this.saver = saver;
        this.objectiveManager = objectiveManager;
        this.playerDataStorage = playerDataStorage;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createObjectiveAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createObjectiveAction(instruction);
    }

    private NullableActionAdapter createObjectiveAction(final Instruction instruction) throws QuestException {
        final String action = instruction.string().map(s -> s.toLowerCase(Locale.ROOT)).get().getValue(null);
        final Argument<List<ObjectiveIdentifier>> objectives = instruction.identifier(ObjectiveIdentifier.class).list().get();
        return new NullableActionAdapter(new ObjectiveAction(plugin, loggerFactory.create(ObjectiveAction.class), profileProvider, saver,
                objectiveManager, playerDataStorage, instruction.getPackage(), objectives, action));
    }
}
