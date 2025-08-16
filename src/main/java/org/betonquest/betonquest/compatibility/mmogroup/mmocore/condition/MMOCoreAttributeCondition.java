package org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;

/**
 * Condition to check if the player has a level in an attribute.
 */
public class MMOCoreAttributeCondition implements PlayerCondition {

    /**
     * Attribute to check.
     */
    private final Variable<PlayerAttribute> attribute;

    /**
     * If the actual must be equal to the target level.
     */
    private final boolean mustBeEqual;

    /**
     * Required level.
     */
    private final Variable<Number> targetLevelVar;

    /**
     * Create a new attribute condition.
     *
     * @param attribute   the attribute to check
     * @param targetLevel the required level
     * @param equal       whether the actual must be equal to the target level
     */
    public MMOCoreAttributeCondition(final Variable<PlayerAttribute> attribute, final Variable<Number> targetLevel, final boolean equal) {
        this.attribute = attribute;
        this.targetLevelVar = targetLevel;
        this.mustBeEqual = equal;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final int targetLevel = targetLevelVar.getValue(profile).intValue();
        final int actualLevel = PlayerData.get(profile.getPlayerUUID()).getAttributes().getAttribute(attribute.getValue(profile));

        return mustBeEqual ? actualLevel == targetLevel : actualLevel >= targetLevel;
    }
}
