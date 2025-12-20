package org.betonquest.betonquest.mc_1_21_4.notify.io;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.notify.NotifyIOFactory;
import org.betonquest.betonquest.notify.io.TotemNotifyIO;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Factory to create {@link TotemNotifyIO}s.
 */
public class UpdatedTotemNotifyIOFactory implements NotifyIOFactory {

    /**
     * Variable processor to create and resolve variables.
     */
    private final Variables variables;

    /**
     * Create a new Totem Notify IO factory.
     *
     * @param variables the variable processor to create and resolve variables
     */
    public UpdatedTotemNotifyIOFactory(final Variables variables) {
        this.variables = variables;
    }

    @Override
    public NotifyIO create(@Nullable final QuestPackage pack, final Map<String, String> categoryData) throws QuestException {
        return new UpdatedTotemNotifyIO(variables, pack, categoryData);
    }
}
