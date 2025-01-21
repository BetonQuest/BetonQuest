package org.betonquest.betonquest.quest.variable.item;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariableAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.item.QuestItem;

import java.util.Locale;

/**
 * A factory for creating Item variables.
 */
public class ItemVariableFactory implements PlayerVariableFactory, PlayerlessVariableFactory {

    /**
     * Create a new Item variable factory.
     */
    public ItemVariableFactory() {
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        return new NullableVariableAdapter(parseInstruction(instruction));
    }

    @Override
    public PlayerlessVariable parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableVariableAdapter(parseInstruction(instruction));
    }

    @SuppressWarnings({"PMD.AvoidLiteralsInIfCondition", "PMD.CyclomaticComplexity"})
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
        final QuestItem questItem;
        if (pos == 3) {
            final String path = instruction.getPart(1) + "." + instruction.getPart(2);
            questItem = instruction.getQuestItem(path);
        } else {
            questItem = instruction.getQuestItem();
        }
        return new ItemVariable(questItem, typeAndAmount.getLeft(), raw, typeAndAmount.getRight());
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
