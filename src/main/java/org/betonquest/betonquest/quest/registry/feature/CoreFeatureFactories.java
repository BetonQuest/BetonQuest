package org.betonquest.betonquest.quest.registry.feature;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.conversation.InventoryConvIO;
import org.betonquest.betonquest.conversation.NonInterceptingInterceptor;
import org.betonquest.betonquest.conversation.SimpleConvIO;
import org.betonquest.betonquest.conversation.SimpleInterceptor;
import org.betonquest.betonquest.conversation.SlowTellrawConvIO;
import org.betonquest.betonquest.conversation.TellrawConvIO;
import org.betonquest.betonquest.notify.ActionBarNotifyIO;
import org.betonquest.betonquest.notify.AdvancementNotifyIO;
import org.betonquest.betonquest.notify.BossBarNotifyIO;
import org.betonquest.betonquest.notify.ChatNotifyIO;
import org.betonquest.betonquest.notify.SoundIO;
import org.betonquest.betonquest.notify.SubTitleNotifyIO;
import org.betonquest.betonquest.notify.SuppressNotifyIO;
import org.betonquest.betonquest.notify.TitleNotifyIO;
import org.betonquest.betonquest.notify.TotemNotifyIO;
import org.betonquest.betonquest.schedule.LastExecutionCache;
import org.betonquest.betonquest.schedule.impl.realtime.cron.RealtimeCronSchedule;
import org.betonquest.betonquest.schedule.impl.realtime.cron.RealtimeCronScheduler;
import org.betonquest.betonquest.schedule.impl.realtime.daily.RealtimeDailySchedule;
import org.betonquest.betonquest.schedule.impl.realtime.daily.RealtimeDailyScheduler;

/**
 * Registers the stuff that is not built from Instructions.
 */
public class CoreFeatureFactories {
    /**
     * Factory to create new class specific loggers.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Cache to catch up missed schedulers.
     */
    private final LastExecutionCache lastExecutionCache;

    /**
     * Create a new Core Other Factories class for registering.
     *
     * @param loggerFactory      the factory to create new class specific loggers
     * @param lastExecutionCache the cache to catch up missed schedulers
     */
    public CoreFeatureFactories(final BetonQuestLoggerFactory loggerFactory, final LastExecutionCache lastExecutionCache) {
        this.loggerFactory = loggerFactory;
        this.lastExecutionCache = lastExecutionCache;
    }

    /**
     * Registers the Factories.
     *
     * @param registries containing the registry to register in
     */
    public void register(final FeatureRegistries registries) {
        final ConversationIORegistry conversationIOTypes = registries.conversationIO();
        conversationIOTypes.register("simple", SimpleConvIO.class);
        conversationIOTypes.register("tellraw", TellrawConvIO.class);
        conversationIOTypes.register("chest", InventoryConvIO.class);
        conversationIOTypes.register("combined", InventoryConvIO.Combined.class);
        conversationIOTypes.register("slowtellraw", SlowTellrawConvIO.class);

        final InterceptorRegistry interceptorTypes = registries.interceptor();
        interceptorTypes.register("simple", SimpleInterceptor.class);
        interceptorTypes.register("none", NonInterceptingInterceptor.class);

        final NotifyIORegistry notifyIOTypes = registries.notifyIO();
        notifyIOTypes.register("suppress", SuppressNotifyIO.class);
        notifyIOTypes.register("chat", ChatNotifyIO.class);
        notifyIOTypes.register("advancement", AdvancementNotifyIO.class);
        notifyIOTypes.register("actionbar", ActionBarNotifyIO.class);
        notifyIOTypes.register("bossbar", BossBarNotifyIO.class);
        notifyIOTypes.register("title", TitleNotifyIO.class);
        notifyIOTypes.register("totem", TotemNotifyIO.class);
        notifyIOTypes.register("subtitle", SubTitleNotifyIO.class);
        notifyIOTypes.register("sound", SoundIO.class);

        final ScheduleRegistry eventSchedulingTypes = registries.eventScheduling();
        eventSchedulingTypes.register("realtime-daily", RealtimeDailySchedule.class,
                new RealtimeDailyScheduler(loggerFactory.create(RealtimeDailyScheduler.class, "Schedules"), lastExecutionCache));
        eventSchedulingTypes.register("realtime-cron", RealtimeCronSchedule.class,
                new RealtimeCronScheduler(loggerFactory.create(RealtimeCronScheduler.class, "Schedules"), lastExecutionCache));
    }
}
