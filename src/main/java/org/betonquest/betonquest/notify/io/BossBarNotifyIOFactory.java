package org.betonquest.betonquest.notify.io;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.notify.NotifyIO;
import org.betonquest.betonquest.notify.NotifyIOFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Factory to create {@link BossBarNotifyIO}s.
 */
public class BossBarNotifyIOFactory implements NotifyIOFactory {
    /**
     * Custom logger to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new Factory.
     *
     * @param loggerFactory the logger Factory to create new class specific logger
     */
    public BossBarNotifyIOFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public NotifyIO create(@Nullable final QuestPackage pack, final Map<String, String> categoryData) throws QuestException {
        return new BossBarNotifyIO(loggerFactory.create(BossBarNotifyIO.class), pack, categoryData);
    }
}
