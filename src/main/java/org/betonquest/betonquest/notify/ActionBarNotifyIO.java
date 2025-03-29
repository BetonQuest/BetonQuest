package org.betonquest.betonquest.notify;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;

import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class ActionBarNotifyIO extends NotifyIO {

    public ActionBarNotifyIO(final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(pack, data);
    }

    @Override
    protected void notifyPlayer(final Component message, final OnlineProfile onlineProfile) {
        onlineProfile.getPlayer().sendActionBar(message);
    }
}
