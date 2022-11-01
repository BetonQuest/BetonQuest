package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class SoundIO extends NotifyIO {

    public SoundIO(final QuestPackage pack, final Map<String, String> data) throws InstructionParseException {
        super(pack, data);
    }

    @Override
    protected void notifyPlayer(final String message, final OnlineProfile onlineProfile) {
        // Empty
    }
}
