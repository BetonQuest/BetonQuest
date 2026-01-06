package org.betonquest.betonquest.compatibility.mythicmobs.event;

import io.lumine.mythic.api.mobs.MythicMob;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicHider;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicMobDoubleParser;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Optional;

/**
 * Factory to create {@link MythicSpawnMobEvent}s from {@link Instruction}s.
 */
public class MythicSpawnMobEventFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Factory to create new class specific loggers.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

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
     * @param plugin          the plugin instance
     * @param mythicHider     the mythic hider instance for the spawned mobs
     */
    public MythicSpawnMobEventFactory(final BetonQuestLoggerFactory loggerFactory, final MythicMobDoubleParser mythicMobParser,
                                      final Plugin plugin, final MythicHider mythicHider) {
        this.loggerFactory = loggerFactory;
        this.mythicMobParser = mythicMobParser;
        this.plugin = plugin;
        this.mythicHider = mythicHider;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Location> loc = instruction.location().get();
        final Argument<Map.Entry<MythicMob, Double>> mobLevel = instruction.parse(mythicMobParser).get();
        final Argument<Number> amount = instruction.number().get();
        final FlagArgument<MythicHider> privateMob = instruction.bool().map(val -> val ? mythicHider : null).getFlag("private", mythicHider);
        final FlagArgument<Boolean> targetPlayer = instruction.bool().getFlag("target", true);
        final Argument<String> marked = instruction.packageIdentifier().get("marked").orElse(null);
        return new OnlineActionAdapter(new MythicSpawnMobEvent(plugin, loc, mobLevel, amount, privateMob, targetPlayer, marked),
                loggerFactory.create(MythicSpawnMobEvent.class), instruction.getPackage());
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        final Argument<Location> loc = instruction.location().get();
        final Argument<Map.Entry<MythicMob, Double>> mobLevel = instruction.parse(mythicMobParser).get();
        final Argument<Number> amount = instruction.number().get();
        final Argument<String> marked = instruction.packageIdentifier().get("marked").orElse(null);
        return new MythicSpawnMobEvent(plugin, loc, mobLevel, amount, profile -> Optional.empty(), profile -> Optional.empty(), marked);
    }
}
