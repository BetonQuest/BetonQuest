package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.QuestCanceler;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Cancels the quest for the player.
 */
@SuppressWarnings("PMD.CommentRequired")
public class CancelEvent extends QuestEvent {

    private final QuestCanceler canceler;

    public CancelEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        canceler = BetonQuest.getCanceler().get(instruction.getPackage().getQuestPath() + "." + instruction.next());
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        canceler.cancel(profile.getOnlineProfile().get());
        return null;
    }

}
