package org.betonquest.betonquest.notify.io;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.notify.NotifyIOFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Factory to create {@link TitleNotifyIO}s.
 */
public class TitleNotifyIOFactory implements NotifyIOFactory {

    /**
     * Variable processor to create and resolve variables.
     */
    private final Variables variables;

    /**
     * Create a new Title Notify IO factory.
     *
     * @param variables the variable processor to create and resolve variables
     */
    public TitleNotifyIOFactory(final Variables variables) {
        this.variables = variables;
    }

    @Override
    public NotifyIO create(@Nullable final QuestPackage pack, final Map<String, String> categoryData) throws QuestException {
        return new TitleNotifyIO(variables, pack, categoryData);
    }
}
