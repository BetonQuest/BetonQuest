package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;

import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class SubTitleNotifyIO extends NotifyIO {
    private final int fadeIn;

    private final int stay;

    private final int fadeOut;

    public SubTitleNotifyIO(final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(pack, data);

        fadeIn = getIntegerData("fadein", 10);
        stay = getIntegerData("stay", 70);
        fadeOut = getIntegerData("fadeout", 20);
    }

    @Override
    protected void notifyPlayer(final String message, final OnlineProfile onlineProfile) {
        onlineProfile.getPlayer().sendTitle(" ", message, fadeIn, stay, fadeOut);
    }
}
