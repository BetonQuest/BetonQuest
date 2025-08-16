package org.betonquest.betonquest.compatibility.mythicmobs.event;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadPlayerlessEvent;
import org.bukkit.Location;

/**
 * Factory to create {@link MythicSpawnMobEvent}s from {@link Instruction}s.
 */
public class MythicSpawnMobEventFactory implements PlayerEventFactory, PlayerlessEventFactory {
    /**
     * Expected format: {@code identifier:amount}.
     */
    private static final int MOB_FORMAT_LENGTH = 2;

    /**
     * API Helper for getting MythicMobs.
     */
    private final BukkitAPIHelper apiHelper;

    /**
     * Data required for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Compatibility instance to check for other hooks.
     */
    private final Compatibility compatibility;

    /**
     * Create a new factory for {@link MythicSpawnMobEvent}s.
     *
     * @param apiHelper     the api helper used get MythicMobs
     * @param data          the primary server thread data required for main thread checking
     * @param compatibility the compatibility instance to check for other hooks
     */
    public MythicSpawnMobEventFactory(final BukkitAPIHelper apiHelper, final PrimaryServerThreadData data,
                                      final Compatibility compatibility) {
        this.apiHelper = apiHelper;
        this.data = data;
        this.compatibility = compatibility;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Location> loc = instruction.get(Argument.LOCATION);
        final String[] mobParts = instruction.next().split(":");
        if (mobParts.length != MOB_FORMAT_LENGTH) {
            throw new QuestException("Wrong mob format");
        }
        final String mob = mobParts[0];
        final Variable<Number> level = instruction.get(mobParts[1], Argument.NUMBER);
        final Variable<Number> amount = instruction.get(Argument.NUMBER);
        final boolean privateMob;
        if (compatibility.getHooked().contains("ProtocolLib")) {
            privateMob = instruction.hasArgument("private");
        } else {
            privateMob = false;
        }
        final boolean targetPlayer = instruction.hasArgument("target");
        final Variable<String> marked = instruction.getValue("marked", PackageArgument.IDENTIFIER);
        return new PrimaryServerThreadEvent(new MythicSpawnMobEvent(apiHelper, data.plugin(), loc, mob, level, amount, privateMob, targetPlayer, marked), data);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        final Variable<Location> loc = instruction.get(Argument.LOCATION);
        final String[] mobParts = instruction.next().split(":");
        if (mobParts.length != MOB_FORMAT_LENGTH) {
            throw new QuestException("Wrong mob format");
        }
        final String mob = mobParts[0];
        final Variable<Number> level = instruction.get(mobParts[1], Argument.NUMBER);
        final Variable<Number> amount = instruction.get(Argument.NUMBER);
        final Variable<String> marked = instruction.getValue("marked", PackageArgument.IDENTIFIER);
        return new PrimaryServerThreadPlayerlessEvent(new MythicSpawnMobEvent(apiHelper, data.plugin(), loc, mob, level, amount, false, false, marked), data);
    }
}
