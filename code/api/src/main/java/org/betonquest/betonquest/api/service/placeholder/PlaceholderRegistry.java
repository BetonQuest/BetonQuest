package org.betonquest.betonquest.api.service.placeholder;

import org.betonquest.betonquest.api.quest.CoreQuestRegistry;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;

/**
 * Stores the placeholder factories.
 *
 * @since 3.0.0
 */
public interface PlaceholderRegistry extends CoreQuestRegistry<PlayerPlaceholder, PlayerlessPlaceholder> {

}
