package org.betonquest.betonquest.id.cancel;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.DefaultIdentifier;
import org.betonquest.betonquest.api.identifier.QuestCancelerIdentifier;

/**
 * The default implementation for {@link QuestCancelerIdentifier}s.
 */
public class DefaultQuestCancelerIdentifier extends DefaultIdentifier implements QuestCancelerIdentifier {

    /**
     * The section name for cancelers.
     */
    public static final String CANCELER_SECTION = "cancel";

    /**
     * Creates a new canceler identifier.
     *
     * @param pack       the package the identifier is related to.
     * @param identifier the identifier of the canceler.
     */
    protected DefaultQuestCancelerIdentifier(final QuestPackage pack, final String identifier) {
        super(pack, identifier);
    }
}
