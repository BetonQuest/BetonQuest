package org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.util.Utils;

/**
 * Factory to create {@link MMOCoreAttributeCondition}s from {@link Instruction}s.
 */
public class MMOCoreAttributeConditionFactory implements PlayerConditionFactory {

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new MMO Core Condition Factory.
     *
     * @param data the data for primary server thread access
     */
    public MMOCoreAttributeConditionFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final String attributeName = instruction.next();
        final PlayerAttribute attribute = Utils.getNN(MMOCore.plugin.attributeManager.get(attributeName),
                "Attribute does not exist: " + attributeName);
        final Variable<Number> targetLevelVar = instruction.get(Argument.NUMBER);
        final boolean mustBeEqual = instruction.hasArgument("equal");
        return new PrimaryServerThreadPlayerCondition(new MMOCoreAttributeCondition(attribute, targetLevelVar, mustBeEqual), data);
    }
}
