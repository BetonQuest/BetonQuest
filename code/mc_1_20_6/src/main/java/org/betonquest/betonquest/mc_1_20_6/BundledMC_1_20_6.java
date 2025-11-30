package org.betonquest.betonquest.mc_1_20_6;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.item.ItemRegistry;
import org.betonquest.betonquest.mc_1_20_6.item.UpdatedSimpleItemFactory;
import org.betonquest.betonquest.mc_1_20_6.item.UpdatedSimpleQuestItemSerializer;

/**
 * Allows to register features with Minecraft 1.20.6.
 */
@SuppressWarnings("PMD.ClassNamingConventions")
public class BundledMC_1_20_6 {

    /**
     * Custom Logger instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Creates a new Object to register Minecraft version changes.
     *
     * @param log the custom logger for this class
     */
    public BundledMC_1_20_6(final BetonQuestLogger log) {
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
        item.register("simple", new UpdatedSimpleItemFactory(betonQuest.getQuestPackageManager(), textParser, bookPageWrapper,
                () -> betonQuest.getPluginConfig().getBoolean("item.quest.lore") ? betonQuest.getPluginMessage() : null));
        item.registerSerializer("simple", new UpdatedSimpleQuestItemSerializer(textParser, bookPageWrapper));
        log.info("Enabled Minecraft 1.20.6 module");
    }
}
