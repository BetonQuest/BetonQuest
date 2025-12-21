package org.betonquest.betonquest.mc_1_21_4.notify.io;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.Placeholders;
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
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

    /**
     * Create a new Totem Notify IO factory.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     */
    public UpdatedTotemNotifyIOFactory(final Placeholders placeholders) {
        this.placeholders = placeholders;
    }

    @Override
    public NotifyIO create(@Nullable final QuestPackage pack, final Map<String, String> categoryData) throws QuestException {
        return new UpdatedTotemNotifyIO(placeholders, pack, categoryData);
    }
}
