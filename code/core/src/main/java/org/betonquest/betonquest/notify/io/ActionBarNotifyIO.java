package org.betonquest.betonquest.notify.io;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.notify.NotifyIO;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Displays the message in the action bar.
 */
public class ActionBarNotifyIO extends NotifyIO {

    /**
     * Create a new Action Bar Notify IO.
     *
     * @param variables the variable processor to create and resolve variables
     * @param pack      the source pack to resolve variables
     * @param data      the customization data for notifications
     * @throws QuestException when data could not be parsed
     */
    public ActionBarNotifyIO(final Variables variables, @Nullable final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(variables, pack, data);
    }

    @Override
    protected void notifyPlayer(final Component message, final OnlineProfile onlineProfile) {
        onlineProfile.getPlayer().sendActionBar(message);
    }
}
