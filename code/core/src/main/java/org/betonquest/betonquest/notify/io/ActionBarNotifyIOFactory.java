package org.betonquest.betonquest.notify.io;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.notify.NotifyIOFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Factory to create {@link ActionBarNotifyIO}s.
 */
public class ActionBarNotifyIOFactory implements NotifyIOFactory {

    /**
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

    /**
     * Creates a new Action Bar Notify IO factory.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     */
    public ActionBarNotifyIOFactory(final Placeholders placeholders) {
        this.placeholders = placeholders;
    }

    @Override
    public NotifyIO create(@Nullable final QuestPackage pack, final Map<String, String> categoryData) throws QuestException {
        return new ActionBarNotifyIO(placeholders, pack, categoryData);
    }
}
