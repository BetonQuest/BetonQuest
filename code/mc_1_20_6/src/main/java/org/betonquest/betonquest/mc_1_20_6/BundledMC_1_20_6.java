package org.betonquest.betonquest.mc_1_20_6;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.item.ItemRegistry;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.mc_1_20_6.item.UpdatedSimpleItemFactory;
import org.betonquest.betonquest.mc_1_20_6.item.UpdatedSimpleQuestItemSerializer;

/**
 * Allows to register features with Minecraft 1.20.6.
 */
@SuppressWarnings("PMD.ClassNamingConventions")
public class BundledMC_1_20_6 implements Integrator {

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
    public void hook(final BetonQuestApi api) {
        final ItemRegistry item = betonQuest.getFeatureRegistries().item();
        final TextParser textParser = betonQuest.getTextParser();
        final BookPageWrapper bookPageWrapper = new BookPageWrapper(betonQuest.getFontRegistry(), 114, 14);
        item.register("simple", new UpdatedSimpleItemFactory(betonQuest.getQuestTypeApi().placeholders(),
                betonQuest.getQuestPackageManager(), textParser, bookPageWrapper,
                () -> betonQuest.getPluginConfig().getBoolean("item.quest.lore") ? betonQuest.getPluginMessage() : null));
        item.registerSerializer("simple", new UpdatedSimpleQuestItemSerializer(textParser, bookPageWrapper));
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
