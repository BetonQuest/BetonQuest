package org.betonquest.betonquest.mc_1_21_4;

import org.apache.commons.lang3.function.TriFunction;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.dependency.CoreComponentLoader;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.service.item.ItemRegistry;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.conversation.menu.MenuConvIOFactory;
import org.betonquest.betonquest.conversation.menu.input.ConversationAction;
import org.betonquest.betonquest.conversation.menu.input.ConversationSession;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.betonquest.betonquest.kernel.registry.feature.NotifyIORegistry;
import org.betonquest.betonquest.mc_1_21_4.conversation.InputEventSession;
import org.betonquest.betonquest.mc_1_21_4.item.UpdatedSimpleItemFactory;
import org.betonquest.betonquest.mc_1_21_4.item.UpdatedSimpleQuestItemSerializer;
import org.betonquest.betonquest.mc_1_21_4.listener.BundleListener;
import org.betonquest.betonquest.mc_1_21_4.notify.io.UpdatedTotemNotifyIOFactory;
import org.betonquest.betonquest.mc_1_21_4.quest.condition.biome.UpdatedBiomeConditionFactory;
import org.bukkit.entity.Player;

/**
 * Allows to register features with Minecraft 1.21.4.
 */
@SuppressWarnings("PMD.ClassNamingConventions")
public class BundledMC_1_21_4 implements Integration {

    /**
     * BetonQuest class to get relevant object from.
     */
    private final BetonQuest betonQuest;

    /**
     * Creates a new Object to register Minecraft version changes.
     *
     * @param betonQuest the BetonQuest class to get relevant object from
     */
    public BundledMC_1_21_4(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final ItemRegistry item = api.items().registry();
        final CoreComponentLoader componentLoader = betonQuest.getComponentLoader();
        final TextParser textParser = componentLoader.get(TextParser.class);
        final BookPageWrapper bookPageWrapper = new BookPageWrapper(api.fonts(), 114, 14);
        item.register("simple", new UpdatedSimpleItemFactory(api.placeholders().manager(),
                api.packages(), textParser, bookPageWrapper,
                () -> betonQuest.getPluginConfig().getBoolean("item.quest.lore") ? betonQuest.getPluginMessage() : null));
        item.registerSerializer("simple", new UpdatedSimpleQuestItemSerializer(textParser, bookPageWrapper));

        final TriFunction<Player, ConversationAction, Boolean, ConversationSession> inputFunction = (player, control, setSpeed)
                -> new InputEventSession(betonQuest, player, control, setSpeed);
        componentLoader.get(ConversationIORegistry.class).register("menu", new MenuConvIOFactory(
                api.loggerFactory(), betonQuest.getPluginConfig(), betonQuest,
                componentLoader.get(PluginMessage.class), inputFunction, componentLoader.get(TextParser.class),
                api.fonts(), betonQuest.getConversationColors()));

        api.conditions().registry().register("biome", new UpdatedBiomeConditionFactory());

        componentLoader.get(NotifyIORegistry.class).register("totem", new UpdatedTotemNotifyIOFactory(api.placeholders().manager()));
        api.bukkit().registerEvents(new BundleListener());
    }

    @Override
    public void postEnable(final BetonQuestApi betonQuestApi) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
