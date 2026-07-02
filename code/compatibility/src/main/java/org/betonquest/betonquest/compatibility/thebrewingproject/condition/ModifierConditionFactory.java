package org.betonquest.betonquest.compatibility.thebrewingproject.condition;

import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.quest.condition.number.Operation;

/**
 * Factory for {@link ModifierCondition}.
 *
 * @param api the TheBrewingProject API
 */
public record ModifierConditionFactory(TheBrewingProjectApi api) implements PlayerConditionFactory {

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> modifierNameArgument = instruction.string().get();
        final Argument<Operation> modifierConditionTypeArgument = instruction.parse(Operation::fromSymbol).get();
        final Argument<Number> modifierValueArgument = instruction.number().get();
        return new ModifierCondition(api, modifierNameArgument, modifierConditionTypeArgument, modifierValueArgument);
    }
}
