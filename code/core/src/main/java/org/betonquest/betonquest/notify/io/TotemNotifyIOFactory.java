package org.betonquest.betonquest.notify.io;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.notify.NotifyIOFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Factory to create {@link TotemNotifyIO}s.
 */
public class TotemNotifyIOFactory implements NotifyIOFactory {

    /**
     * Create a new factory.
     */
    public TotemNotifyIOFactory() {
    }

    @Override
    public NotifyIO create(@Nullable final QuestPackage pack, final Map<String, String> categoryData) throws QuestException {
        return new TotemNotifyIO(pack, categoryData);
    }
}
