package org.betonquest.betonquest.notify.io;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.notify.NotifyIOFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Factory to create {@link SubTitleNotifyIO}s.
 */
public class SubTitleNotifyIOFactory implements NotifyIOFactory {

    /**
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

    /**
     * Create a new Sub Title Notify IO factory.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     */
    public SubTitleNotifyIOFactory(final Placeholders placeholders) {
        this.placeholders = placeholders;
    }

    @Override
    public NotifyIO create(@Nullable final QuestPackage pack, final Map<String, String> categoryData) throws QuestException {
        return new SubTitleNotifyIO(placeholders, pack, categoryData);
    }
}
