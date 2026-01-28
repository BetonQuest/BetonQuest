package org.betonquest.betonquest.feature;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.api.text.TextParserRegistry;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.interceptor.NonInterceptingInterceptorFactory;
import org.betonquest.betonquest.conversation.interceptor.SimpleInterceptorFactory;
import org.betonquest.betonquest.conversation.io.InventoryConvIOFactory;
import org.betonquest.betonquest.conversation.io.SimpleConvIOFactory;
import org.betonquest.betonquest.conversation.io.SlowTellrawConvIOFactory;
import org.betonquest.betonquest.conversation.io.TellrawConvIOFactory;
import org.betonquest.betonquest.item.SimpleQuestItemFactory;
import org.betonquest.betonquest.item.SimpleQuestItemSerializer;
import org.betonquest.betonquest.kernel.registry.feature.BaseFeatureRegistries;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.betonquest.betonquest.kernel.registry.feature.InterceptorRegistry;
import org.betonquest.betonquest.kernel.registry.feature.ItemTypeRegistry;
import org.betonquest.betonquest.kernel.registry.feature.NotifyIORegistry;
import org.betonquest.betonquest.kernel.registry.feature.ScheduleRegistry;
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
import org.betonquest.betonquest.schedule.impl.realtime.cron.RealtimeCronScheduleFactory;
import org.betonquest.betonquest.schedule.impl.realtime.cron.RealtimeCronScheduler;
import org.betonquest.betonquest.schedule.impl.realtime.daily.RealtimeDailyScheduleFactory;
import org.betonquest.betonquest.schedule.impl.realtime.daily.RealtimeDailyScheduler;
import org.betonquest.betonquest.text.parser.LegacyParser;
import org.betonquest.betonquest.text.parser.MineDownParser;
import org.betonquest.betonquest.text.parser.MiniMessageParser;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

/**
 * Registers the stuff that is not built from Instructions.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class CoreFeatureFactories {

    /**
     * Factory to create new class specific loggers.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packManager;

    /**
     * Cache to catch up missed schedulers.
     */
    private final LastExecutionCache lastExecutionCache;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * The Config.
     */
    private final ConfigAccessor config;

    /**
     * The colors to use for the conversation.
     */
    private final ConversationColors colors;

    /**
     * The message parser to use for parsing messages.
     */
    private final TextParser textParser;

    /**
     * The font registry to use in APIs that work with {@link net.kyori.adventure.text.Component}.
     */
    private final FontRegistry fontRegistry;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Create a new Core Other Factories class for registering.
     *
     * @param loggerFactory      the factory to create new class specific loggers
     * @param packManager        the quest package manager to get quest packages from
     * @param lastExecutionCache the cache to catch up missed schedulers
     * @param questTypeApi       the class for executing actions
     * @param placeholders       the {@link Placeholders} to create and resolve placeholders
     * @param featureApi         the Feature API
     * @param config             the config
     * @param colors             the colors to use for the conversation
     * @param textParser         the text parser to use for parsing text
     * @param fontRegistry       the font registry to use for the conversation
     * @param pluginMessage      the {@link PluginMessage} instance
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public CoreFeatureFactories(final BetonQuestLoggerFactory loggerFactory, final QuestPackageManager packManager,
                                final LastExecutionCache lastExecutionCache, final QuestTypeApi questTypeApi,
                                final Placeholders placeholders, final FeatureApi featureApi,
                                final ConfigAccessor config, final ConversationColors colors,
                                final TextParser textParser, final FontRegistry fontRegistry, final PluginMessage pluginMessage) {
        this.loggerFactory = loggerFactory;
        this.packManager = packManager;
        this.lastExecutionCache = lastExecutionCache;
        this.questTypeApi = questTypeApi;
        this.placeholders = placeholders;
        this.featureApi = featureApi;
        this.config = config;
        this.colors = colors;
        this.textParser = textParser;
        this.fontRegistry = fontRegistry;
        this.pluginMessage = pluginMessage;
    }

    /**
     * Registers the Factories.
     *
     * @param registries containing the registry to register in
     */
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    public void register(final BaseFeatureRegistries registries) {
        final ConversationIORegistry conversationIOTypes = registries.conversationIO();
        conversationIOTypes.register("simple", new SimpleConvIOFactory(colors));
        conversationIOTypes.register("tellraw", new TellrawConvIOFactory(colors));
        conversationIOTypes.register("chest", new InventoryConvIOFactory(loggerFactory, placeholders, packManager, config, fontRegistry, colors, false));
        conversationIOTypes.register("combined", new InventoryConvIOFactory(loggerFactory, placeholders, packManager, config, fontRegistry, colors, true));
        conversationIOTypes.register("slowtellraw", new SlowTellrawConvIOFactory(fontRegistry, colors));

        final InterceptorRegistry interceptorTypes = registries.interceptor();
        interceptorTypes.register("simple", new SimpleInterceptorFactory());
        interceptorTypes.register("none", new NonInterceptingInterceptorFactory());

        final ItemTypeRegistry itemTypes = registries.item();
        final BookPageWrapper bookPageWrapper = new BookPageWrapper(fontRegistry, 114, 14);
        itemTypes.register("simple", new SimpleQuestItemFactory(placeholders, packManager, textParser, bookPageWrapper,
                () -> config.getBoolean("item.quest.lore") ? pluginMessage : null));
        itemTypes.registerSerializer("simple", new SimpleQuestItemSerializer(textParser, bookPageWrapper));

        final Plugin plugin = BetonQuest.getInstance();
        final NotifyIORegistry notifyIOTypes = registries.notifyIO();
        notifyIOTypes.register("suppress", new SuppressNotifyIOFactory());
        notifyIOTypes.register("chat", new ChatNotifyIOFactory(placeholders, featureApi.conversationApi()));
        notifyIOTypes.register("advancement", new AdvancementNotifyIOFactory(placeholders, plugin));
        notifyIOTypes.register("actionbar", new ActionBarNotifyIOFactory(placeholders));
        notifyIOTypes.register("bossbar", new BossBarNotifyIOFactory(placeholders, plugin));
        notifyIOTypes.register("title", new TitleNotifyIOFactory(placeholders));
        notifyIOTypes.register("totem", new TotemNotifyIOFactory(placeholders));
        notifyIOTypes.register("subtitle", new SubTitleNotifyIOFactory(placeholders));
        notifyIOTypes.register("sound", new SoundIOFactory(placeholders));

        final ScheduleRegistry schedulingTypes = registries.actionScheduling();
        schedulingTypes.register("realtime-daily", new RealtimeDailyScheduleFactory(),
                new RealtimeDailyScheduler(loggerFactory.create(RealtimeDailyScheduler.class, "Schedules"), questTypeApi, lastExecutionCache)
        );
        schedulingTypes.register("realtime-cron", new RealtimeCronScheduleFactory(),
                new RealtimeCronScheduler(loggerFactory.create(RealtimeCronScheduler.class, "Schedules"), questTypeApi, lastExecutionCache)
        );

        final TextParserRegistry textParserRegistry = registries.textParser();
        registerTextParsers(textParserRegistry);
    }

    private void registerTextParsers(final TextParserRegistry textParserRegistry) {
        final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder()
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .extractUrls()
                .build();
        textParserRegistry.register("legacy", new LegacyParser(legacySerializer));
        final MiniMessage miniMessage = MiniMessage.miniMessage();
        textParserRegistry.register("minimessage", new MiniMessageParser(miniMessage));
        final MiniMessage legacyMiniMessage = MiniMessage.builder()
                .preProcessor(input -> {
                    final TextComponent deserialize = legacySerializer.deserialize(ChatColor.translateAlternateColorCodes('&', input.replaceAll("(?<!\\\\)\\\\n", "\n")));
                    final String serialize = miniMessage.serialize(deserialize);
                    return serialize.replaceAll("\\\\<", "<");
                })
                .build();
        textParserRegistry.register("legacyminimessage", new MiniMessageParser(legacyMiniMessage));
        textParserRegistry.register("minedown", new MineDownParser());
    }
}
