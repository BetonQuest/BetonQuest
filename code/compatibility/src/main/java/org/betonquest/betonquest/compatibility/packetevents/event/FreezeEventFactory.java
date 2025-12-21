package org.betonquest.betonquest.compatibility.packetevents.event;

import com.github.retrooper.packetevents.PacketEventsAPI;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
import org.bukkit.plugin.Plugin;

/**
 * Factory to create {@link FreezeEvent}s from {@link Instruction}s.
 */
public class FreezeEventFactory implements PlayerEventFactory {

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The PacketEvents API instance.
     */
    private final PacketEventsAPI<?> packetEventsAPI;

    /**
     * Logger factory to create class specific logger for quest type factories.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new freeze event factory.
     *
     * @param plugin          the plugin instance
     * @param packetEventsAPI the PacketEvents API instance
     * @param loggerFactory   the logger factory to create new class specific logger
     * @param data            the data for primary server thread access
     */
    public FreezeEventFactory(final Plugin plugin, final PacketEventsAPI<?> packetEventsAPI, final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.plugin = plugin;
        this.packetEventsAPI = packetEventsAPI;
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Number> ticks = instruction.get(instruction.getParsers().number().validate(value -> value.doubleValue() < 1));
        final BetonQuestLogger log = loggerFactory.create(FreezeEvent.class);
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new FreezeEvent(plugin, packetEventsAPI, ticks),
                log, instruction.getPackage()), data);
    }
}
