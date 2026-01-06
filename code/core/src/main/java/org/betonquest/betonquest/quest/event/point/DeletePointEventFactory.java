package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.Saver;

/**
 * Factory to create delete points events from {@link Instruction}s.
 */
public class DeletePointEventFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Database saver to use for writing offline player data.
     */
    private final Saver saver;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Create the delete points event factory.
     *
     * @param dataStorage     the storage providing player data
     * @param saver           the saver to use
     * @param profileProvider the profile provider instance
     */
    public DeletePointEventFactory(final PlayerDataStorage dataStorage, final Saver saver, final ProfileProvider profileProvider) {
        this.dataStorage = dataStorage;
        this.saver = saver;
        this.profileProvider = profileProvider;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return new DeletePointEvent(dataStorage::getOffline, instruction.packageIdentifier().get());
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        final Argument<String> category = instruction.packageIdentifier().get();
        return new DeletePointPlayerlessEvent(dataStorage, saver, profileProvider, category);
    }
}
