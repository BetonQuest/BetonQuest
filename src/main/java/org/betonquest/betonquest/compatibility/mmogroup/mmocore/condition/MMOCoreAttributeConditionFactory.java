package org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition;

import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.MMOCoreUtils;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link MMOCoreAttributeCondition}s from {@link Instruction}s.
 */
public class MMOCoreAttributeConditionFactory implements PlayerConditionFactory {

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Util class to get attributes.
     */
    private final MMOCoreUtils mmoCoreUtils;

    /**
     * Create a new MMO Core Condition Factory.
     *
     * @param data         the data for primary server thread access
     * @param mmoCoreUtils the utils class to get attributes
     */
    public MMOCoreAttributeConditionFactory(final PrimaryServerThreadData data, final MMOCoreUtils mmoCoreUtils) {
        this.data = data;
        this.mmoCoreUtils = mmoCoreUtils;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final PlayerAttribute attribute = mmoCoreUtils.getAttribute(instruction.next());
        final VariableNumber targetLevelVar = instruction.get(VariableNumber::new);
        final boolean mustBeEqual = instruction.hasArgument("equal");
        return new PrimaryServerThreadPlayerCondition(new MMOCoreAttributeCondition(attribute, targetLevelVar, mustBeEqual), data);
    }
}
