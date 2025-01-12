package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.QuestException;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class SuppressNotifyIO extends NotifyIO {

    public SuppressNotifyIO(final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(pack, new HashMap<>());
        data.clear();
    }

    @Override
    protected void notifyPlayer(final String message, final OnlineProfile onlineProfile) {
        // Empty
    }
}
