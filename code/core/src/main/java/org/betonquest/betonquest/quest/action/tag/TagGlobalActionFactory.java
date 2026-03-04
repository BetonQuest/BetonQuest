package org.betonquest.betonquest.quest.action.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.database.GlobalData;

import java.util.List;
import java.util.Locale;

/**
 * Factory to create global tag actions from {@link Instruction}s.
 */
public class TagGlobalActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * The global data handling global tags.
     */
    private final GlobalData globalData;

    /**
     * Create the global tag action factory.
     *
     * @param globalData the global data
     */
    public TagGlobalActionFactory(final GlobalData globalData) {
        this.globalData = globalData;
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
            case "add" -> createPlayerlessAddTagAction(tags);
            case "delete", "del" -> createPlayerlessDeleteTagAction(tags);
            default -> throw new QuestException("Unknown tag action: " + action);
        };
    }

    private PlayerlessAction createPlayerlessAddTagAction(final Argument<List<String>> tags) {
        final TagChanger tagChanger = new AddTagChanger(tags);
        return new PlayerlessTagAction(globalData.tags(), tagChanger);
    }

    private PlayerlessAction createPlayerlessDeleteTagAction(final Argument<List<String>> tags) {
        final TagChanger tagChanger = new DeleteTagChanger(tags);
        return new PlayerlessTagAction(globalData.tags(), tagChanger);
    }

    private PlayerAction createAddTagAction(final Argument<List<String>> tags) {
        final TagChanger tagChanger = new AddTagChanger(tags);
        return new TagAction(profile -> globalData.tags(), tagChanger);
    }

    private PlayerAction createDeleteTagAction(final Argument<List<String>> tags) {
        final TagChanger tagChanger = new DeleteTagChanger(tags);
        return new TagAction(profile -> globalData.tags(), tagChanger);
    }
}
