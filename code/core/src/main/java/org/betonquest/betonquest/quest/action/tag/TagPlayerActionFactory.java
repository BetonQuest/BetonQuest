package org.betonquest.betonquest.quest.action.tag;

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
import org.betonquest.betonquest.quest.action.DoNothingPlayerlessAction;

import java.util.List;
import java.util.Locale;

/**
 * Factory to create tag actions from {@link Instruction}s.
 */
public class TagPlayerActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * The saver to inject into database-using actions.
     */
    private final Saver saver;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Create the tag player action factory.
     *
     * @param dataStorage     the storage providing player data
     * @param saver           database saver to use
     * @param profileProvider the profile provider instance
     */
    public TagPlayerActionFactory(final PlayerDataStorage dataStorage, final Saver saver, final ProfileProvider profileProvider) {
        this.dataStorage = dataStorage;
        this.saver = saver;
        this.profileProvider = profileProvider;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final String action = instruction.string().get().getValue(null);
        final Argument<List<String>> tags = instruction.packageIdentifier().list().get();
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "add" -> createAddTagAction(tags);
            case "delete", "del" -> createDeleteTagAction(tags);
            default -> throw new QuestException("Unknown tag action: " + action);
        };
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        final String action = instruction.string().get().getValue(null);
        final Argument<List<String>> tags = instruction.packageIdentifier().list().get();
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "add" -> new DoNothingPlayerlessAction();
            case "delete", "del" -> new DeleteTagPlayerlessAction(dataStorage, saver, profileProvider, tags);
            default -> throw new QuestException("Unknown tag action: " + action);
        };
    }

    private TagAction createAddTagAction(final Argument<List<String>> tags) {
        final TagChanger tagChanger = new AddTagChanger(tags);
        return new TagAction(dataStorage::getOffline, tagChanger);
    }

    private TagAction createDeleteTagAction(final Argument<List<String>> tags) {
        final TagChanger tagChanger = new DeleteTagChanger(tags);
        return new TagAction(dataStorage::getOffline, tagChanger);
    }
}
