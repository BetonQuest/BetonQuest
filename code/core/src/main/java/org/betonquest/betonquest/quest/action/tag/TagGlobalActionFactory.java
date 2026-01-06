package org.betonquest.betonquest.quest.action.tag;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;

import java.util.List;
import java.util.Locale;

/**
 * Factory to create global tag actions from {@link Instruction}s.
 */
public class TagGlobalActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * BetonQuest instance to provide to actions.
     */
    private final BetonQuest betonQuest;

    /**
     * Create the global tag action factory.
     *
     * @param betonQuest BetonQuest instance to pass on
     */
    public TagGlobalActionFactory(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
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
            case "add" -> createStaticAddTagAction(tags);
            case "delete", "del" -> createStaticDeleteTagAction(tags);
            default -> throw new QuestException("Unknown tag action: " + action);
        };
    }

    private PlayerlessAction createStaticAddTagAction(final Argument<List<String>> tags) {
        final TagChanger tagChanger = new AddTagChanger(tags);
        return new PlayerlessTagAction(betonQuest.getGlobalData(), tagChanger);
    }

    private PlayerlessAction createStaticDeleteTagAction(final Argument<List<String>> tags) {
        final TagChanger tagChanger = new DeleteTagChanger(tags);
        return new PlayerlessTagAction(betonQuest.getGlobalData(), tagChanger);
    }

    private PlayerAction createAddTagAction(final Argument<List<String>> tags) {
        final TagChanger tagChanger = new AddTagChanger(tags);
        return new TagAction(profile -> betonQuest.getGlobalData(), tagChanger);
    }

    private PlayerAction createDeleteTagAction(final Argument<List<String>> tags) {
        final TagChanger tagChanger = new DeleteTagChanger(tags);
        return new TagAction(profile -> betonQuest.getGlobalData(), tagChanger);
    }
}
