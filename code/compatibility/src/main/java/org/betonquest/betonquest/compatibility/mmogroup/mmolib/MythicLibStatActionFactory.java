package org.betonquest.betonquest.compatibility.mmogroup.mmolib;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.FlagState;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

import java.util.Optional;

/**
 * Factory to create {@link MythicLibStatAction}s from {@link Instruction}s.
 */
public class MythicLibStatActionFactory implements PlayerActionFactory {

    /**
     * Create a new factory for the Mythic Lib Action.
     */
    public MythicLibStatActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Optional<Argument<String>> statName = instruction.string().get("stat");
        final Argument<Number> value = instruction.number().get("value", 0);
        final Argument<String> key = instruction.string().get("key", io.lumine.mythic.lib.command.argument.Argument.DEFAULT_MODIFIER_KEY);
        final Argument<ModifierType> type = instruction.enumeration(ModifierType.class).get("type", ModifierType.FLAT);
        final Argument<EquipmentSlot> slot = instruction.enumeration(EquipmentSlot.class).get("slot", EquipmentSlot.OTHER);
        final Argument<ModifierSource> source = instruction.enumeration(ModifierSource.class).get("source", ModifierSource.OTHER);
        final FlagArgument<Boolean> add = instruction.bool().getFlag("add", true);
        final FlagArgument<Boolean> remove = instruction.bool().getFlag("remove", true);
        final FlagArgument<Boolean> clear = instruction.bool().getFlag("clear", true);

        if (add.getState() == FlagState.ABSENT && remove.getState() == FlagState.ABSENT && clear.getState() == FlagState.ABSENT) {
            throw new QuestException("At least one of the flags 'add', 'remove' or 'clear' must be set.");
        }

        if (statName.isEmpty() && clear.getState() == FlagState.ABSENT) {
            throw new QuestException("The stat name must be set if not clearing");
        }

        return new MythicLibStatAction(statName.orElse(null), value, key, type, slot, source,
                add, remove, clear);
    }
}
