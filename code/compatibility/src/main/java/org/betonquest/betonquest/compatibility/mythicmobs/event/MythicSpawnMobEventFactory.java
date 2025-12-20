package org.betonquest.betonquest.compatibility.mythicmobs.event;

import io.lumine.mythic.api.mobs.MythicMob;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadPlayerlessEvent;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicHider;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicMobDoubleParser;
import org.bukkit.Location;

import java.util.Map;

/**
 * Factory to create {@link MythicSpawnMobEvent}s from {@link DefaultInstruction}s.
 */
public class MythicSpawnMobEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Factory to create new class specific loggers.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data required for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Mythic Hider instance.
     */
    private final MythicHider mythicHider;

    /**
     *
     * Parses valid {@link MythicMob} from string.
     */
    private final MythicMobDoubleParser mythicMobParser;

    /**
     * Create a new factory for {@link MythicSpawnMobEvent}s.
     *
     * @param loggerFactory   the logger factory to create class specific logger
     * @param mythicMobParser the parser for the mob type
     * @param data            the primary server thread data required for main thread checking
     * @param mythicHider     the mythic hider instance for the spawned mobs
     */
    public MythicSpawnMobEventFactory(final BetonQuestLoggerFactory loggerFactory, final MythicMobDoubleParser mythicMobParser,
                                      final PrimaryServerThreadData data, final MythicHider mythicHider) {
        this.loggerFactory = loggerFactory;
        this.mythicMobParser = mythicMobParser;
        this.data = data;
        this.mythicHider = mythicHider;
    }

    @Override
    public PlayerEvent parsePlayer(final DefaultInstruction instruction) throws QuestException {
        final Variable<Location> loc = instruction.get(Argument.LOCATION);
        final Variable<Map.Entry<MythicMob, Double>> mobLevel = instruction.get(mythicMobParser);
        final Variable<Number> amount = instruction.get(Argument.NUMBER);
        final MythicHider privateMob = instruction.hasArgument("private") ? mythicHider : null;
        final boolean targetPlayer = instruction.hasArgument("target");
        final Variable<String> marked = instruction.getValue("marked", PackageArgument.IDENTIFIER);
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new MythicSpawnMobEvent(data.plugin(), loc, mobLevel, amount, privateMob, targetPlayer, marked),
                loggerFactory.create(MythicSpawnMobEvent.class),
                instruction.getPackage()
        ), data);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final DefaultInstruction instruction) throws QuestException {
        final Variable<Location> loc = instruction.get(Argument.LOCATION);
        final Variable<Map.Entry<MythicMob, Double>> mobLevel = instruction.get(mythicMobParser);
        final Variable<Number> amount = instruction.get(Argument.NUMBER);
        final Variable<String> marked = instruction.getValue("marked", PackageArgument.IDENTIFIER);
        return new PrimaryServerThreadPlayerlessEvent(new MythicSpawnMobEvent(data.plugin(), loc, mobLevel, amount, null, false, marked), data);
    }
}
