package org.betonquest.betonquest.quest.variable.item;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.api.quest.variable.online.OnlineVariableAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Factory to create item durability variables from {@link Instruction}s.
 */
public class ItemDurabilityVariableFactory implements PlayerVariableFactory {

    /**
     * The key for digits.
     */
    private static final String DIGITS_KEY = "digits";

    /**
     * The default amount of digits after comma.
     */
    private static final int DEFAULT_DIGITS = 2;

    /**
     * Create the item durability variable factory.
     */
    public ItemDurabilityVariableFactory() {
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<EquipmentSlot> slot = instruction.get(Argument.ENUM(EquipmentSlot.class));
        final boolean relative = instruction.hasArgument("relative");
        final Variable<Number> digitsAfter = digits(instruction);
        final boolean inPercent = instruction.hasArgument("percent");
        return new OnlineVariableAdapter(new ItemDurabilityVariable(slot, relative, digitsAfter, inPercent));
    }

    private Variable<Number> digits(final Instruction instruction) throws QuestException {
        if (instruction.hasArgument(DIGITS_KEY)) {
            for (int i = instruction.size() - 2; i >= 0; i--) {
                final String part = instruction.getPart(i);
                if (DIGITS_KEY.equalsIgnoreCase(part)) {
                    return instruction.get(instruction.getPart(i + 1), Argument.NUMBER, DEFAULT_DIGITS);
                }
            }
        }
        return new Variable<>(DEFAULT_DIGITS);
    }
}
