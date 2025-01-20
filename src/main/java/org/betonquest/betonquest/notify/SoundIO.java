package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;

import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class SoundIO extends NotifyIO {

    public SoundIO(final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(pack, data);
    }

    @Override
    protected void notifyPlayer(final String message, final OnlineProfile onlineProfile) {
        // Empty
    }
}
