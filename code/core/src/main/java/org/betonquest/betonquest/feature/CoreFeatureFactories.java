package org.betonquest.betonquest.feature;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.message.MessageParserRegistry;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.interceptor.NonInterceptingInterceptorFactory;
import org.betonquest.betonquest.conversation.interceptor.SimpleInterceptorFactory;
import org.betonquest.betonquest.conversation.io.InventoryConvIOFactory;
import org.betonquest.betonquest.conversation.io.SimpleConvIOFactory;
import org.betonquest.betonquest.conversation.io.SlowTellrawConvIOFactory;
import org.betonquest.betonquest.conversation.io.TellrawConvIOFactory;
import org.betonquest.betonquest.item.SimpleQuestItemFactory;
import org.betonquest.betonquest.item.SimpleQuestItemSerializer;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.betonquest.betonquest.kernel.registry.feature.FeatureRegistries;
import org.betonquest.betonquest.kernel.registry.feature.InterceptorRegistry;
import org.betonquest.betonquest.kernel.registry.feature.ItemTypeRegistry;
import org.betonquest.betonquest.kernel.registry.feature.NotifyIORegistry;
import org.betonquest.betonquest.kernel.registry.feature.ScheduleRegistry;
import org.betonquest.betonquest.message.parser.LegacyParser;
import org.betonquest.betonquest.message.parser.MineDownMessageParser;
import org.betonquest.betonquest.message.parser.MiniMessageParser;
import org.betonquest.betonquest.notify.SuppressNotifyIOFactory;
import org.betonquest.betonquest.notify.io.ActionBarNotifyIOFactory;
import org.betonquest.betonquest.notify.io.AdvancementNotifyIOFactory;
import org.betonquest.betonquest.notify.io.BossBarNotifyIOFactory;
import org.betonquest.betonquest.notify.io.ChatNotifyIOFactory;
import org.betonquest.betonquest.notify.io.SoundIOFactory;
import org.betonquest.betonquest.notify.io.SubTitleNotifyIOFactory;
import org.betonquest.betonquest.notify.io.TitleNotifyIOFactory;
import org.betonquest.betonquest.notify.io.TotemNotifyIOFactory;
import org.betonquest.betonquest.schedule.LastExecutionCache;
import org.betonquest.betonquest.schedule.impl.realtime.cron.RealtimeCronSchedule;
import org.betonquest.betonquest.schedule.impl.realtime.cron.RealtimeCronScheduler;
import org.betonquest.betonquest.schedule.impl.realtime.daily.RealtimeDailySchedule;
import org.betonquest.betonquest.schedule.impl.realtime.daily.RealtimeDailyScheduler;
import org.bukkit.ChatColor;

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
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * The Config.
     */
    private final ConfigAccessor config;

    /**
     * The colors to use for the conversation.
     */
    private final ConversationColors colors;

    /**
     * The font registry to use in APIs that work with {@link net.kyori.adventure.text.Component}.
     */
    private final FontRegistry fontRegistry;

    /**
     * Create a new Core Other Factories class for registering.
     *
     * @param loggerFactory      the factory to create new class specific loggers
     * @param lastExecutionCache the cache to catch up missed schedulers
     * @param questTypeAPI       the class for executing events
     * @param config             the config
     * @param colors             the colors to use for the conversation
     * @param fontRegistry       the font registry to use for the conversation
     */
    public CoreFeatureFactories(final BetonQuestLoggerFactory loggerFactory, final LastExecutionCache lastExecutionCache,
                                final QuestTypeAPI questTypeAPI, final ConfigAccessor config, final ConversationColors colors,
                                final FontRegistry fontRegistry) {
        this.loggerFactory = loggerFactory;
        this.lastExecutionCache = lastExecutionCache;
        this.questTypeAPI = questTypeAPI;
        this.config = config;
        this.colors = colors;
        this.fontRegistry = fontRegistry;
    }

    /**
     * Registers the Factories.
     *
     * @param registries containing the registry to register in
     */
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    public void register(final FeatureRegistries registries) {
        final ConversationIORegistry conversationIOTypes = registries.conversationIO();
        conversationIOTypes.register("simple", new SimpleConvIOFactory(colors));
        conversationIOTypes.register("tellraw", new TellrawConvIOFactory(colors));
        conversationIOTypes.register("chest", new InventoryConvIOFactory(loggerFactory, config, fontRegistry, colors, false));
        conversationIOTypes.register("combined", new InventoryConvIOFactory(loggerFactory, config, fontRegistry, colors, true));
        conversationIOTypes.register("slowtellraw", new SlowTellrawConvIOFactory(fontRegistry, colors));

        final InterceptorRegistry interceptorTypes = registries.interceptor();
        interceptorTypes.register("simple", new SimpleInterceptorFactory());
        interceptorTypes.register("none", new NonInterceptingInterceptorFactory());

        final ItemTypeRegistry itemTypes = registries.item();
        itemTypes.register("simple", new SimpleQuestItemFactory());
        itemTypes.registerSerializer("simple", new SimpleQuestItemSerializer());

        final NotifyIORegistry notifyIOTypes = registries.notifyIO();
        notifyIOTypes.register("suppress", new SuppressNotifyIOFactory());
        notifyIOTypes.register("chat", new ChatNotifyIOFactory());
        notifyIOTypes.register("advancement", new AdvancementNotifyIOFactory());
        notifyIOTypes.register("actionbar", new ActionBarNotifyIOFactory());
        notifyIOTypes.register("bossbar", new BossBarNotifyIOFactory());
        notifyIOTypes.register("title", new TitleNotifyIOFactory());
        notifyIOTypes.register("totem", new TotemNotifyIOFactory());
        notifyIOTypes.register("subtitle", new SubTitleNotifyIOFactory());
        notifyIOTypes.register("sound", new SoundIOFactory());

        final ScheduleRegistry eventSchedulingTypes = registries.eventScheduling();
        eventSchedulingTypes.register("realtime-daily", RealtimeDailySchedule.class, new RealtimeDailyScheduler(
                loggerFactory.create(RealtimeDailyScheduler.class, "Schedules"), questTypeAPI, lastExecutionCache));
        eventSchedulingTypes.register("realtime-cron", RealtimeCronSchedule.class, new RealtimeCronScheduler(
                loggerFactory.create(RealtimeCronScheduler.class, "Schedules"), questTypeAPI, lastExecutionCache));

        final MessageParserRegistry messageParserRegistry = registries.messageParser();
        registerMessageParsers(messageParserRegistry);
    }

    private void registerMessageParsers(final MessageParserRegistry messageParserRegistry) {
        final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder()
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .extractUrls()
                .build();
        messageParserRegistry.register("legacy", new LegacyParser(legacySerializer));
        final MiniMessage miniMessage = MiniMessage.miniMessage();
        messageParserRegistry.register("minimessage", new MiniMessageParser(miniMessage));
        final MiniMessage legacyMiniMessage = MiniMessage.builder()
                .preProcessor(input -> {
                    final TextComponent deserialize = legacySerializer.deserialize(ChatColor.translateAlternateColorCodes('&', input.replaceAll("(?<!\\\\)\\\\n", "\n")));
                    final String serialize = miniMessage.serialize(deserialize);
                    return serialize.replaceAll("\\\\<", "<");
                })
                .build();
        messageParserRegistry.register("legacyminimessage", new MiniMessageParser(legacyMiniMessage));
        messageParserRegistry.register("minedown", new MineDownMessageParser());
    }
}
