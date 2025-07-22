package org.betonquest.betonquest.notify.io;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.notify.NotifyIOFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Factory to create {@link AdvancementNotifyIO}s.
 */
public class AdvancementNotifyIOFactory implements NotifyIOFactory {

    /**
     * Empty default constructor.
     */
    public AdvancementNotifyIOFactory() {
    }

    @Override
    public NotifyIO create(@Nullable final QuestPackage pack, final Map<String, String> categoryData) throws QuestException {
        return new AdvancementNotifyIO(pack, categoryData);
    }
}
