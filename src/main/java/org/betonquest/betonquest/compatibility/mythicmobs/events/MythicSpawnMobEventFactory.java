package org.betonquest.betonquest.compatibility.mythicmobs.events;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadStaticEvent;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.utils.Utils;

/**
 * Factory to create {@link MythicSpawnMobEvent}s from {@link Instruction}s.
 */
public class MythicSpawnMobEventFactory implements EventFactory, StaticEventFactory {
    /**
     * Expected format: identifier:amount
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
     * Variable processor to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create a new factory for {@link MythicSpawnMobEvent}s.
     *
     * @param apiHelper         the api helper used get MythicMobs
     * @param data              the primary server thread data required for main thread checking
     * @param variableProcessor the variable processor to create new variables with
     */
    public MythicSpawnMobEventFactory(final BukkitAPIHelper apiHelper, final PrimaryServerThreadData data, final VariableProcessor variableProcessor) {
        this.apiHelper = apiHelper;
        this.data = data;
        this.variableProcessor = variableProcessor;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final VariableLocation loc = instruction.getLocation();
        final String[] mobParts = instruction.next().split(":");
        if (mobParts.length != MOB_FORMAT_LENGTH) {
            throw new QuestException("Wrong mob format");
        }
        final String mob = mobParts[0];
        final VariableNumber level = instruction.getVarNum(mobParts[1]);
        final VariableNumber amount = instruction.getVarNum();
        final boolean privateMob;
        if (Compatibility.getHooked().contains("ProtocolLib")) {
            privateMob = instruction.hasArgument("private");
        } else {
            privateMob = false;
        }
        final boolean targetPlayer = instruction.hasArgument("target");
        final String markedString = instruction.getOptional("marked");
        final VariableString marked = markedString == null ? null : new VariableString(
                variableProcessor,
                instruction.getPackage(),
                Utils.addPackage(instruction.getPackage(), markedString)
        );
        return new PrimaryServerThreadEvent(new MythicSpawnMobEvent(apiHelper, loc, mob, level, amount, privateMob, targetPlayer, marked), data);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        final VariableLocation loc = instruction.getLocation();
        final String[] mobParts = instruction.next().split(":");
        if (mobParts.length != MOB_FORMAT_LENGTH) {
            throw new QuestException("Wrong mob format");
        }
        final String mob = mobParts[0];
        final VariableNumber level = instruction.getVarNum(mobParts[1]);
        final VariableNumber amount = instruction.getVarNum();
        final String markedString = instruction.getOptional("marked");
        final VariableString marked = markedString == null ? null : new VariableString(
                variableProcessor,
                instruction.getPackage(),
                Utils.addPackage(instruction.getPackage(), markedString)
        );
        return new PrimaryServerThreadStaticEvent(new MythicSpawnMobEvent(apiHelper, loc, mob, level, amount, false, false, marked), data);
    }
}
