package org.betonquest.betonquest.compatibility.packetevents.event;

import com.github.retrooper.packetevents.PacketEventsAPI;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;
import org.bukkit.plugin.Plugin;

/**
 * Factory to create {@link FreezeEvent}s from {@link Instruction}s.
 */
public class FreezeEventFactory implements PlayerActionFactory {

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
     * Create a new freeze event factory.
     *
     * @param plugin          the plugin instance
     * @param packetEventsAPI the PacketEvents API instance
     * @param loggerFactory   the logger factory to create new class specific logger
     */
    public FreezeEventFactory(final Plugin plugin, final PacketEventsAPI<?> packetEventsAPI, final BetonQuestLoggerFactory loggerFactory) {
        this.plugin = plugin;
        this.packetEventsAPI = packetEventsAPI;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> ticks = instruction.number().atLeast(1).get();
        final BetonQuestLogger log = loggerFactory.create(FreezeEvent.class);
        return new OnlineActionAdapter(new FreezeEvent(plugin, packetEventsAPI, ticks), log, instruction.getPackage());
    }
}
