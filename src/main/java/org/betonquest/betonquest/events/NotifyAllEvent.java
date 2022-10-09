package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;

@SuppressWarnings("PMD.CommentRequired")
public class NotifyAllEvent extends NotifyEvent {

    public NotifyAllEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction);
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {

        for (final Profile onlineProfile : PlayerConverter.getOnlineProfiles()) {
            super.execute(onlineProfile);
        }
        return null;
    }
}
