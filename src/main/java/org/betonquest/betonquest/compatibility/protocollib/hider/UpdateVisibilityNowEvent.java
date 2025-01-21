package org.betonquest.betonquest.compatibility.protocollib.hider;

import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;

@SuppressWarnings("PMD.CommentRequired")
public class UpdateVisibilityNowEvent extends QuestEvent {

    public UpdateVisibilityNowEvent(final Instruction instruction) throws QuestException {
        super(instruction, true);
    }

    @Override
    protected Void execute(final Profile profile) throws QuestException {
        if (NPCHider.getInstance() != null) {
            NPCHider.getInstance().applyVisibility(profile.getOnlineProfile().get());
        }
        return null;
    }
}
