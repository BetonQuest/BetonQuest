package org.betonquest.betonquest.kernel.registry.quest;

import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.placeholder.PlaceholderRegistry;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;
import org.betonquest.betonquest.kernel.processor.adapter.PlaceholderAdapter;
import org.betonquest.betonquest.kernel.processor.adapter.PlaceholderAdapterFactory;
import org.betonquest.betonquest.kernel.registry.QuestTypeRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * Stores the placeholder types that can be used in BetonQuest.
 */
public class PlaceholderTypeRegistry extends QuestTypeRegistry<PlayerPlaceholder, PlayerlessPlaceholder, PlaceholderAdapter>
        implements PlaceholderRegistry {

    /**
     * Create a new placeholder type registry.
     *
     * @param log the logger that will be used for logging
     */
    public PlaceholderTypeRegistry(final BetonQuestLogger log) {
        super(log, "placeholder");
    }

    @Override
    protected TypeFactory<PlaceholderAdapter> getFactoryAdapter(
            @Nullable final PlayerQuestFactory<PlayerPlaceholder> playerFactory,
            @Nullable final PlayerlessQuestFactory<PlayerlessPlaceholder> playerlessFactory) {
        return new PlaceholderAdapterFactory(playerFactory, playerlessFactory);
    }
}
