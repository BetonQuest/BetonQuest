package org.betonquest.betonquest.id.action;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.DefaultReadableIdentifier;

/**
 * The default implementation for {@link ActionIdentifier}s.
 */
public class DefaultActionIdentifier extends DefaultReadableIdentifier implements ActionIdentifier {

    /**
     * The section in the configuration where actions are defined.
     */
    public static final String ACTION_SECTION = "actions";

    /**
     * Creates a new Action Identifier.
     *
     * @param pack       the package of the action.
     * @param identifier the identifier of the action.
     */
    protected DefaultActionIdentifier(final QuestPackage pack, final String identifier) {
        super(pack, identifier, ACTION_SECTION);
    }
}
