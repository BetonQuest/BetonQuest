package org.betonquest.betonquest.notify.io;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.notify.NotifyIOFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Factory to create {@link ActionBarNotifyIO}s.
 */
public class ActionBarNotifyIOFactory implements NotifyIOFactory {

    /**
     * Variable processor to create and resolve variables.
     */
    private final Variables variables;

    /**
     * Creates a new Action Bar Notify IO factory.
     *
     * @param variables the variable processor to create and resolve variables
     */
    public ActionBarNotifyIOFactory(final Variables variables) {
        this.variables = variables;
    }

    @Override
    public NotifyIO create(@Nullable final QuestPackage pack, final Map<String, String> categoryData) throws QuestException {
        return new ActionBarNotifyIO(variables, pack, categoryData);
    }
}
