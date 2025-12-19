package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Factory to create {@link SuppressNotifyIO}s.
 */
public class SuppressNotifyIOFactory implements NotifyIOFactory {

    /**
     * Empty default constructor.
     */
    public SuppressNotifyIOFactory() {
    }

    @Override
    public NotifyIO create(@Nullable final QuestPackage pack, final Map<String, String> categoryData) throws QuestException {
        return new SuppressNotifyIO(pack, categoryData);
    }
}
