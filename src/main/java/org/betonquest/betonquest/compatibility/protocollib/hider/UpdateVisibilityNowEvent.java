package org.betonquest.betonquest.compatibility.protocollib.hider;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;

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
