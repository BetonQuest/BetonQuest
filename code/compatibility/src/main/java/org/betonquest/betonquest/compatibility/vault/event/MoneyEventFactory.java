package org.betonquest.betonquest.compatibility.vault.event;

import net.milkbowl.vault.economy.Economy;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.betonquest.betonquest.quest.event.NotificationLevel;

/**
 * Factory to create {@link MoneyEvent}s from {@link Instruction}s.
 */
public class MoneyEventFactory implements PlayerActionFactory {

    /**
     * Economy where the balance will be modified.
     */
    private final Economy economy;

    /**
     * Logger factory to create new logger instances.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Create a new Factory to create Vault Money Events.
     *
     * @param economy       the economy where the balance will be modified
     * @param loggerFactory the logger factory to create new logger instances.
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public MoneyEventFactory(final Economy economy, final BetonQuestLoggerFactory loggerFactory, final PluginMessage pluginMessage) {
        this.economy = economy;
        this.loggerFactory = loggerFactory;
        this.pluginMessage = pluginMessage;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final FlagArgument<Boolean> multi = instruction.bool().getFlag("multi", true);
        final Argument<Number> amount = instruction.number().get();
        final FlagArgument<Boolean> notify = instruction.bool().getFlag("notify", true);
        final IngameNotificationSender givenSender;
        final IngameNotificationSender takenSender;
        if (notify.getValue(null).orElse(false)) {
            final QuestPackage pack = instruction.getPackage();
            final String fullID = instruction.getID().getFull();
            final BetonQuestLogger log = loggerFactory.create(MoneyEvent.class);
            givenSender = new IngameNotificationSender(log, pluginMessage, pack, fullID, NotificationLevel.INFO, "money_given");
            takenSender = new IngameNotificationSender(log, pluginMessage, pack, fullID, NotificationLevel.INFO, "money_taken");
        } else {
            givenSender = null;
            takenSender = null;
        }

        return new MoneyEvent(economy, amount, multi, givenSender, takenSender);
    }
}
