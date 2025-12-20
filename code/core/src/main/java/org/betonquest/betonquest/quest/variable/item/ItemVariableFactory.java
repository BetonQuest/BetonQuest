package org.betonquest.betonquest.quest.variable.item;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.Item;
import org.betonquest.betonquest.api.instruction.argument.InstructionIdentifierArgument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariableAdapter;
import org.betonquest.betonquest.data.PlayerDataStorage;

import java.util.Locale;

/**
 * A factory for creating Item variables.
 */
public class ItemVariableFactory implements PlayerVariableFactory, PlayerlessVariableFactory {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * Create a new Item variable factory.
     *
     * @param playerDataStorage the storage for player data
     */
    public ItemVariableFactory(final PlayerDataStorage playerDataStorage) {
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        return new NullableVariableAdapter(parseInstruction(instruction));
    }

    @Override
    public PlayerlessVariable parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableVariableAdapter(parseInstruction(instruction));
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private ItemVariable parseInstruction(final Instruction instruction) throws QuestException {
        final boolean raw;
        int pos = instruction.size() - 1;
        if ("raw".equalsIgnoreCase(instruction.getPart(pos))) {
            raw = true;
            pos--;
        } else {
            raw = false;
        }
        final String argument = instruction.getPart(pos).toLowerCase(Locale.ROOT);
        final Pair<ItemDisplayType, Integer> typeAndAmount = getTypeAndAmount(argument);
        final Variable<Item> questItem = instruction.get(InstructionIdentifierArgument.ITEM);
        return new ItemVariable(playerDataStorage, questItem, typeAndAmount.getLeft(), raw, typeAndAmount.getRight());
    }

    @SuppressWarnings({"PMD.AvoidLiteralsInIfCondition", "PMD.CyclomaticComplexity"})
    private Pair<ItemDisplayType, Integer> getTypeAndAmount(final String argument) throws QuestException {
        final ItemDisplayType type;
        int amount = 0;
        if (argument.startsWith("left:")) {
            type = ItemDisplayType.LEFT;
            try {
                amount = Integer.parseInt(argument.substring(5));
            } catch (final NumberFormatException e) {
                throw new QuestException("Could not parse item amount", e);
            }
        } else if ("amount".equals(argument)) {
            type = ItemDisplayType.AMOUNT;
        } else if ("name".equals(argument)) {
            type = ItemDisplayType.NAME;
        } else if (argument.startsWith("lore:")) {
            type = ItemDisplayType.LORE;
            try {
                amount = Integer.parseInt(argument.substring(5));
            } catch (final NumberFormatException e) {
                throw new QuestException("Could not parse line", e);
            }
        } else {
            throw new QuestException(String.format("Unknown argument type: '%s'",
                    argument));
        }
        return Pair.of(type, amount);
    }
}
