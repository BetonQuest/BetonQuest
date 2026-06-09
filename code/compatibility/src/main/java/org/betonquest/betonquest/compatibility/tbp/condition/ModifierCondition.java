package org.betonquest.betonquest.compatibility.tbp.condition;

import dev.jsinco.brewery.api.effect.DrunkState;
import dev.jsinco.brewery.api.effect.modifier.DrunkenModifier;
import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.quest.condition.number.Operation;

/**
 * A drunken modifier condition.
 *
 * @param api The brewing project api
 */
public record ModifierCondition(TheBrewingProjectApi api) implements PlayerConditionFactory {

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> modifierNameArgument = instruction.string().get();
        final Argument<Operation> modifierConditionTypeArgument = instruction.enumeration(Operation.class).get();
        final Argument<Number> modifierValueArgument = instruction.number().get();
        return playerProfile -> {
            final String modifierName = modifierNameArgument.getValue(playerProfile);
            final DrunkenModifier modifier = api.getModifierManager().getModifier(modifierName)
                    .orElseThrow(() -> new QuestException(String.format("Unknown modifier %s", modifierName)));
            final Operation operation = modifierConditionTypeArgument.getValue(playerProfile);
            final double modifierValue = modifierValueArgument.getValue(playerProfile).doubleValue();
            if (modifierValue < modifier.minValue() || modifierValue > modifier.maxValue()) {
                throw new QuestException(String.format("%s level can only be between %s and %s", modifier.name(), modifier.minValue(), modifier.maxValue()));
            }
            final DrunkState drunkState = api.getDrunksManager().getDrunkState(playerProfile.getPlayerUUID());
            final double actualValue;
            if (drunkState == null) {
                actualValue = modifier.minValue();
            } else {
                actualValue = drunkState.modifierValue(modifier);
            }
            return operation.check(modifierValue, actualValue);
        };
    }
}
