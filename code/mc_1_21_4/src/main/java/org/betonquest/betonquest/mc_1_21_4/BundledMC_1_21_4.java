package org.betonquest.betonquest.mc_1_21_4;

import org.apache.commons.lang3.function.TriFunction;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.conversation.menu.MenuConvIOFactory;
import org.betonquest.betonquest.conversation.menu.input.ConversationAction;
import org.betonquest.betonquest.conversation.menu.input.ConversationSession;
import org.betonquest.betonquest.item.ItemRegistry;
import org.betonquest.betonquest.mc_1_21_4.conversation.InputEventSession;
import org.betonquest.betonquest.mc_1_21_4.item.UpdatedSimpleItemFactory;
import org.betonquest.betonquest.mc_1_21_4.item.UpdatedSimpleQuestItemSerializer;
import org.betonquest.betonquest.mc_1_21_4.notify.io.UpdatedTotemNotifyIOFactory;
import org.betonquest.betonquest.mc_1_21_4.quest.condition.biome.UpdatedBiomeConditionFactory;
import org.bukkit.entity.Player;

/**
 * Allows to register features with Minecraft 1.21.4.
 */
@SuppressWarnings("PMD.ClassNamingConventions")
public class BundledMC_1_21_4 {

    /**
     * Custom Logger instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Creates a new Object to register Minecraft version changes.
     *
     * @param log the custom logger for this class
     */
    public BundledMC_1_21_4(final BetonQuestLogger log) {
        this.log = log;
    }

    /**
     * Registers the Factories.
     *
     * @param betonQuest the BetonQuest class to get relevant object from
     */
    public void register(final BetonQuest betonQuest) {
        final ItemRegistry item = betonQuest.getFeatureRegistries().item();
        final TextParser textParser = betonQuest.getTextParser();
        final BookPageWrapper bookPageWrapper = new BookPageWrapper(betonQuest.getFontRegistry(), 114, 14);
        item.register("simple", new UpdatedSimpleItemFactory(betonQuest.getQuestTypeApi().variables(),
                betonQuest.getQuestPackageManager(), textParser, bookPageWrapper,
                () -> betonQuest.getPluginConfig().getBoolean("item.quest.lore") ? betonQuest.getPluginMessage() : null));
        item.registerSerializer("simple", new UpdatedSimpleQuestItemSerializer(textParser, bookPageWrapper));

        final TriFunction<Player, ConversationAction, Boolean, ConversationSession> inputFunction = (player, control, setSpeed)
                -> new InputEventSession(betonQuest, player, control, setSpeed);
        betonQuest.getFeatureRegistries().conversationIO()
                .register("menu", new MenuConvIOFactory(inputFunction, betonQuest, betonQuest.getTextParser(), betonQuest.getFontRegistry(),
                        betonQuest.getPluginConfig(), betonQuest.getConversationColors()));

        betonQuest.getQuestRegistries().condition().register("biome", new UpdatedBiomeConditionFactory(betonQuest.getLoggerFactory(), betonQuest.getPrimaryServerThreadData()));

        betonQuest.getFeatureRegistries().notifyIO().register("totem", new UpdatedTotemNotifyIOFactory(betonQuest.getVariableProcessor()));
        log.info("Enabled Minecraft 1.21.4 module");
    }
}
