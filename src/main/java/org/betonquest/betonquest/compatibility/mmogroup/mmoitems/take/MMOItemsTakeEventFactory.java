package org.betonquest.betonquest.compatibility.mmogroup.mmoitems.take;

import net.Indyuce.mmoitems.api.Type;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.MMOItemsUtils;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
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
     */
    public MMOItemsTakeEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        super(loggerFactory);
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final BetonQuestLogger log = loggerFactory.create(MMOItemsTakeEvent.class);
        final List<CheckType> checkOrder = getCheckOrder(instruction);
        final Type itemType = MMOItemsUtils.getMMOItemType(instruction.next());
        final String itemID = instruction.next();
        final VariableNumber deleteAmountVar = instruction.get(instruction.getOptional("amount", "1"), VariableNumber::new);
        final NotificationSender notificationSender = getNotificationSender(instruction, log);
        return new OnlineEventAdapter(new MMOItemsTakeEvent(itemType, itemID, deleteAmountVar, checkOrder, notificationSender), log, instruction.getPackage());
    }
}
