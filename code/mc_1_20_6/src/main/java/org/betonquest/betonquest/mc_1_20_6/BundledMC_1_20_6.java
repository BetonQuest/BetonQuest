package org.betonquest.betonquest.mc_1_20_6;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.service.item.ItemRegistry;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.mc_1_20_6.item.UpdatedSimpleItemFactory;
import org.betonquest.betonquest.mc_1_20_6.item.UpdatedSimpleQuestItemSerializer;

/**
 * Allows to register features with Minecraft 1.20.6.
 */
@SuppressWarnings("PMD.ClassNamingConventions")
public class BundledMC_1_20_6 implements Integration {

    /**
     * BetonQuest class to get relevant object from.
     */
    private final BetonQuest betonQuest;

    /**
     * Creates a new Object to register Minecraft version changes.
     *
     * @param betonQuest the BetonQuest class to get relevant object from
     */
    public BundledMC_1_20_6(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final ItemRegistry item = api.items().registry();
        final TextParser textParser = betonQuest.getComponentLoader().get(TextParser.class);
        final BookPageWrapper bookPageWrapper = new BookPageWrapper(api.fonts(), 114, 14);
        item.register("simple", new UpdatedSimpleItemFactory(api.placeholders().manager(),
                api.packages(), textParser, bookPageWrapper,
                () -> betonQuest.getPluginConfig().getBoolean("item.quest.lore") ? api.localizations() : null));
        item.registerSerializer("simple", new UpdatedSimpleQuestItemSerializer(textParser, bookPageWrapper));
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
