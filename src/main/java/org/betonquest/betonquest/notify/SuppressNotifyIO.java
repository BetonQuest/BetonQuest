package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class SuppressNotifyIO extends NotifyIO {

    public SuppressNotifyIO(final QuestPackage pack, final Map<String, String> data) throws InstructionParseException {
        super(pack, new HashMap<>());
        data.clear();
    }

    @Override
    protected void notifyPlayer(final String message, final OnlineProfile onlineProfile) {
        // Empty
    }
}
