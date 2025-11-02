package org.betonquest.betonquest.notify;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Notify IO which clears all current notifications.
 */
public class SuppressNotifyIO extends NotifyIO {

    /**
     * Create a new Suppress Notify IO.
     *
     * @param pack the source pack to resolve variables
     * @param data the data to clear
     * @throws QuestException when data could not be parsed
     */
    public SuppressNotifyIO(@Nullable final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(pack, new HashMap<>());
        data.clear();
    }

    @Override
    protected void notifyPlayer(final Component message, final OnlineProfile onlineProfile) {
        // Empty
    }
}
