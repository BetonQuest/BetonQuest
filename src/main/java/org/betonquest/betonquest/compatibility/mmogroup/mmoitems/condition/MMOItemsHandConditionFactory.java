package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.condition;

import net.Indyuce.mmoitems.api.Type;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.MMOItemsUtils;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link MMOItemsHandCondition}s from {@link Instruction}s.
 */
public class MMOItemsHandConditionFactory implements PlayerConditionFactory {

    /**
     * The offhand key.
     */
    private static final String OFFHAND_KEY = "offhand";

    /**
     * Logger Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for MMO Item Conditions.
     *
     * @param loggerFactory the logger factory to create class specific logger
     * @param data          the data for primary server thread access
     */

    public MMOItemsHandConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Type itemType = MMOItemsUtils.getMMOItemType(instruction.next());
        final String itemID = instruction.next();

        Variable<Number> amount = new Variable<>(1);
        boolean offhand = false;
        while (instruction.hasNext()) {
            final String next = instruction.next();
            if (OFFHAND_KEY.equals(next)) {
                offhand = true;
            } else {
                amount = instruction.get(next, Argument.NUMBER);
            }
        }
        final BetonQuestLogger log = loggerFactory.create(MMOItemsHandCondition.class);
        return new PrimaryServerThreadPlayerCondition(new OnlineConditionAdapter(
                new MMOItemsHandCondition(itemType, itemID, offhand, amount),
                log, instruction.getPackage()), data);
    }
}
