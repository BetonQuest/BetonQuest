package org.betonquest.betonquest.compatibility.mythicmobs.event;

import io.lumine.mythic.api.mobs.MythicMob;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadPlayerlessEvent;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicMobDoubleParser;
import org.betonquest.betonquest.compatibility.protocollib.hider.MythicHider;
import org.bukkit.Location;

import java.util.Map;

/**
 * Factory to create {@link MythicSpawnMobEvent}s from {@link Instruction}s.
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
     * Compatibility instance to check for other hooks.
     */
    private final Compatibility compatibility;

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
     * @param compatibility   the compatibility instance to check for other hooks
     */
    public MythicSpawnMobEventFactory(final BetonQuestLoggerFactory loggerFactory, final MythicMobDoubleParser mythicMobParser, final PrimaryServerThreadData data,
                                      final Compatibility compatibility) {
        this.loggerFactory = loggerFactory;
        this.mythicMobParser = mythicMobParser;
        this.data = data;
        this.compatibility = compatibility;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Location> loc = instruction.get(Argument.LOCATION);
        final Variable<Map.Entry<MythicMob, Double>> mobLevel = instruction.get(mythicMobParser);
        final Variable<Number> amount = instruction.get(Argument.NUMBER);
        final MythicHider privateMob;
        if (compatibility.getHooked().contains("ProtocolLib") && instruction.hasArgument("private")) {
            privateMob = MythicHider.getInstance();
            if (privateMob == null) {
                throw new QuestException("Can't spawn MythicMob private: There is no hider!");
            }
        } else {
            privateMob = null;
        }
        final boolean targetPlayer = instruction.hasArgument("target");
        final Variable<String> marked = instruction.getValue("marked", PackageArgument.IDENTIFIER);
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new MythicSpawnMobEvent(data.plugin(), loc, mobLevel, amount, privateMob, targetPlayer, marked),
                loggerFactory.create(MythicSpawnMobEvent.class),
                instruction.getPackage()
        ), data);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        final Variable<Location> loc = instruction.get(Argument.LOCATION);
        final Variable<Map.Entry<MythicMob, Double>> mobLevel = instruction.get(mythicMobParser);
        final Variable<Number> amount = instruction.get(Argument.NUMBER);
        final Variable<String> marked = instruction.getValue("marked", PackageArgument.IDENTIFIER);
        return new PrimaryServerThreadPlayerlessEvent(new MythicSpawnMobEvent(data.plugin(), loc, mobLevel, amount, null, false, marked), data);
    }
}
