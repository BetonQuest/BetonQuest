package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Factory to create {@link NotifyIO}s.
 */
@FunctionalInterface
public interface NotifyIOFactory {

    /**
     * Create the Notify IO.
     *
     * @param pack         the source pack
     * @param categoryData the configuration data
     * @return the created Notify IO
     * @throws QuestException when the creation fails
     */
    NotifyIO create(@Nullable QuestPackage pack, Map<String, String> categoryData) throws QuestException;
}
