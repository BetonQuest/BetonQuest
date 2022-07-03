package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.tag.AddTagChanger;
import org.betonquest.betonquest.quest.event.tag.DeleteTagChanger;
import org.betonquest.betonquest.quest.event.tag.TagChanger;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.Utils;

/**
 * Adds or removes tags from the player
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CommentRequired"})
public class TagEvent extends QuestEvent {


    private final String[] tags;
    private final boolean add;

    /**
     * Tags changer that will add or remove the defined tags.
     */
    protected final TagChanger tagChanger;

    public TagEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        persistent = true;
        staticness = true;
        add = "add".equalsIgnoreCase(instruction.next());
        tags = instruction.getArray();
        for (int i = 0; i < tags.length; i++) {
            tags[i] = Utils.addPackage(instruction.getPackage(), tags[i]);
        }
        if (add) {
            this.tagChanger = new AddTagChanger(tags);
        } else {
            this.tagChanger = new DeleteTagChanger(tags);
        }
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    @Override
    protected Void execute(final Profile profile) {
        if (profile == null) {
            if (!add) {
                for (final Profile onlineProfile : PlayerConverter.getOnlineProfiles()) {
                    final PlayerData playerData = BetonQuest.getInstance().getPlayerData(onlineProfile);
                    tagChanger.changeTags(playerData);
                }
                for (final String tag : tags) {
                    BetonQuest.getInstance().getSaver().add(new Saver.Record(UpdateType.REMOVE_ALL_TAGS, tag));
                }
            }
        } else {
            final PlayerData playerData = BetonQuest.getInstance().getOfflinePlayerData(profile);
            tagChanger.changeTags(playerData);
        }
        return null;
    }
}
