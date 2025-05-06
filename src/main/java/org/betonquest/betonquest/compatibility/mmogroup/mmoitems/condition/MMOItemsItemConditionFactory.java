package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.condition;

import net.Indyuce.mmoitems.api.Type;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.MMOItemsUtils;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

/**
 * Factory to create {@link MMOItemsItemCondition}s from {@link Instruction}s.
 */
public class MMOItemsItemConditionFactory implements PlayerConditionFactory {

    /**
     * Logger Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for MMO Item Conditions.
     *
     * @param loggerFactory     the logger factory to create class specific logger
     * @param playerDataStorage the storage providing player data
     * @param data              the data for primary server thread access
     */
    public MMOItemsItemConditionFactory(final BetonQuestLoggerFactory loggerFactory, final PlayerDataStorage playerDataStorage, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.playerDataStorage = playerDataStorage;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Type> itemType = instruction.get(MMOItemsUtils::getMMOItemType);
        final Variable<String> itemID = instruction.get(Argument.STRING);
        final Variable<Number> amount = instruction.hasNext() ? instruction.get(Argument.NUMBER) : new Variable<>(1);

        final BetonQuestLogger log = loggerFactory.create(MMOItemsItemCondition.class);
        return new PrimaryServerThreadPlayerCondition(new OnlineConditionAdapter(
                new MMOItemsItemCondition(playerDataStorage, itemType, itemID, amount),
                log, instruction.getPackage()), data);
    }
}
