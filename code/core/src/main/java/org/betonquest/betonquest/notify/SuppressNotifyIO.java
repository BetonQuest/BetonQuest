package org.betonquest.betonquest.notify;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.kernel.processor.adapter.VariableAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Notify IO which clears all current notifications.
 */
public class SuppressNotifyIO extends NotifyIO {

    /**
     * Empty variables to satisfy object structure.
     */
    private static final Variables EMPTY_VARIABLES = new Variables() {
        @Override
        public VariableAdapter create(@Nullable final QuestPackage pack, final String instruction) throws QuestException {
            throw new QuestException("Not implemented");
        }

        @Override
        public String getValue(final QuestPackage pack, final String name, @Nullable final Profile profile) throws QuestException {
            throw new QuestException("Not implemented");
        }

        @Override
        public String getValue(final String variable, @Nullable final Profile profile) throws QuestException {
            throw new QuestException("Not implemented");
        }
    };

    /**
     * Create a new Suppress Notify IO.
     *
     * @param pack the source pack to resolve variables
     * @param data the data to clear
     * @throws QuestException when data could not be parsed
     */
    public SuppressNotifyIO(@Nullable final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(EMPTY_VARIABLES, pack, new HashMap<>());
        data.clear();
    }

    @Override
    protected void notifyPlayer(final Component message, final OnlineProfile onlineProfile) {
        // Empty
    }
}
