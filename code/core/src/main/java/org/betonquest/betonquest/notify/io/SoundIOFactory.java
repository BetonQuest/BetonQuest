package org.betonquest.betonquest.notify.io;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.notify.NotifyIOFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Factory to create {@link SoundIO}s.
 */
public class SoundIOFactory implements NotifyIOFactory {

    /**
     * Variable processor to create and resolve variables.
     */
    private final Variables variables;

    /**
     * Create a new Sound IO factory.
     *
     * @param variables the variable processor to create and resolve variables
     */
    public SoundIOFactory(final Variables variables) {
        this.variables = variables;
    }

    @Override
    public NotifyIO create(@Nullable final QuestPackage pack, final Map<String, String> categoryData) throws QuestException {
        return new SoundIO(variables, pack, categoryData);
    }
}
