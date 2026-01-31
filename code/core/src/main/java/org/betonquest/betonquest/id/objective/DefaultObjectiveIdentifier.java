package org.betonquest.betonquest.id.objective;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.DefaultReadableIdentifier;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;

/**
 * The default implementation for {@link ObjectiveIdentifier}s.
 */
public class DefaultObjectiveIdentifier extends DefaultReadableIdentifier implements ObjectiveIdentifier {

    /**
     * The section in the configuration where objectives are defined.
     */
    public static final String OBJECTIVE_SECTION = "objectives";

    /**
     * Creates a new objective identifier.
     *
     * @param pack       the package of the objective.
     * @param identifier the identifier of the objective.
     */
    protected DefaultObjectiveIdentifier(final QuestPackage pack, final String identifier) {
        super(pack, identifier, OBJECTIVE_SECTION);
    }
}
