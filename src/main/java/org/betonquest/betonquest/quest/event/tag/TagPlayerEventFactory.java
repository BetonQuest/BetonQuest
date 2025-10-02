package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.quest.event.DoNothingPlayerlessEvent;

import java.util.List;
import java.util.Locale;

/**
 * Factory to create tag events from {@link Instruction}s.
 */
public class TagPlayerEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * The saver to inject into database-using events.
     */
    private final Saver saver;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Create the tag player event factory.
     *
     * @param dataStorage     the storage providing player data
     * @param saver           database saver to use
     * @param profileProvider the profile provider instance
     */
    public TagPlayerEventFactory(final PlayerDataStorage dataStorage, final Saver saver, final ProfileProvider profileProvider) {
        this.dataStorage = dataStorage;
        this.saver = saver;
        this.profileProvider = profileProvider;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final String action = instruction.get(Argument.STRING).getValue(null);
        final Variable<List<String>> tags = instruction.getList(PackageArgument.IDENTIFIER);
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "add" -> createAddTagEvent(tags);
            case "delete", "del" -> createDeleteTagEvent(tags);
            default -> throw new QuestException("Unknown tag action: " + action);
        };
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        final String action = instruction.get(Argument.STRING).getValue(null);
        final Variable<List<String>> tags = instruction.getList(PackageArgument.IDENTIFIER);
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "add" -> new DoNothingPlayerlessEvent();
            case "delete", "del" -> new DeleteTagPlayerlessEvent(dataStorage, saver, profileProvider, tags);
            default -> throw new QuestException("Unknown tag action: " + action);
        };
    }

    private TagEvent createAddTagEvent(final Variable<List<String>> tags) {
        final TagChanger tagChanger = new AddTagChanger(tags);
        return new TagEvent(dataStorage::getOffline, tagChanger);
    }

    private TagEvent createDeleteTagEvent(final Variable<List<String>> tags) {
        final TagChanger tagChanger = new DeleteTagChanger(tags);
        return new TagEvent(dataStorage::getOffline, tagChanger);
    }
}
