package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.event;

import net.Indyuce.mmoitems.api.Type;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.MMOItemsUtils;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.event.NotificationSender;
import org.betonquest.betonquest.quest.event.take.AbstractTakeEventFactory;
import org.betonquest.betonquest.quest.event.take.CheckType;

import java.util.List;

/**
 * Factory for MMOItems take events.
 */
public class MMOItemsTakeEventFactory extends AbstractTakeEventFactory {

    /**
     * Create the MMOItems take event factory.
     *
     * @param loggerFactory logger factory to use
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public MMOItemsTakeEventFactory(final BetonQuestLoggerFactory loggerFactory, final PluginMessage pluginMessage) {
        super(loggerFactory, pluginMessage);
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final BetonQuestLogger log = loggerFactory.create(MMOItemsTakeEvent.class);
        final List<CheckType> checkOrder = getCheckOrder(instruction);
        final Type itemType = MMOItemsUtils.getMMOItemType(instruction.next());
        final String itemID = instruction.next();
        MMOItemsUtils.getMMOItemStack(itemType, itemID);
        final Variable<Number> deleteAmountVar = instruction.getValue("amount", Argument.NUMBER, 1);
        final NotificationSender notificationSender = getNotificationSender(instruction, log);
        return new OnlineEventAdapter(new MMOItemsTakeEvent(itemType, itemID, deleteAmountVar, checkOrder, notificationSender), log, instruction.getPackage());
    }
}
