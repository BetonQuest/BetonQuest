package org.betonquest.betonquest.compatibility.shopkeepers;

import com.nisovin.shopkeepers.api.ShopkeepersAPI;
import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
import org.betonquest.betonquest.util.Utils;

import java.util.UUID;

/**
 * Factory to create {@link OpenShopEvent}s from {@link Instruction}s.
 */
public class OpenShopEventFactory implements PlayerEventFactory {

    /**
     * Logger Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new open shop event factory.
     *
     * @param loggerFactory the logger factory to create class specific logger
     * @param data          the data for primary server thread access
     */
    public OpenShopEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final String string = instruction.get(Argument.STRING).getValue(null);
        final Shopkeeper shopkeeper;
        try {
            shopkeeper = Utils.getNN(ShopkeepersAPI.getShopkeeperRegistry().getShopkeeperByUniqueId(UUID.fromString(string)),
                    "Shopkeeper with this UUID does not exist: '" + string + "'");
        } catch (final IllegalArgumentException e) {
            throw new QuestException("Could not parse UUID: '" + string + "'", e);
        }
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new OpenShopEvent(shopkeeper),
                loggerFactory.create(OpenShopEvent.class), instruction.getPackage()), data);
    }
}
