package org.betonquest.betonquest.id.condition;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.DefaultReadableIdentifier;

/**
 * The default implementation for {@link ConditionIdentifier}s.
 */
public class DefaultConditionIdentifier extends DefaultReadableIdentifier implements ConditionIdentifier {

    /**
     * The section in the configuration where conditions are defined.
     */
    public static final String CONDITION_SECTION = "conditions";

    /**
     * If the condition is inverted.
     */
    private final boolean inverted;

    /**
     * Creates a new Condition Identifier.
     *
     * @param pack       the package of the condition.
     * @param identifier the identifier of the condition.
     * @param inverted   if the condition should be interpreted inverted.
     */
    protected DefaultConditionIdentifier(final QuestPackage pack, final String identifier, final boolean inverted) {
        super(pack, identifier, CONDITION_SECTION);
        this.inverted = inverted;
    }

    @Override
    public boolean isInverted() {
        return inverted;
    }
}
