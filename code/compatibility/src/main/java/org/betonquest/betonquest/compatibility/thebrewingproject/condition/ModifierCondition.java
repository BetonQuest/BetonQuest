package org.betonquest.betonquest.compatibility.thebrewingproject.condition;

import dev.jsinco.brewery.api.effect.DrunkState;
import dev.jsinco.brewery.api.effect.modifier.DrunkenModifier;
import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.quest.condition.number.Operation;

/**
 * A drunken modifier condition.
 *
 * @param api                           the brewing project api
 * @param modifierNameArgument          a modifier name argument
 * @param modifierConditionTypeArgument a modifier condition argument
 * @param modifierValueArgument         a modifier value argument
 */
public record ModifierCondition(TheBrewingProjectApi api,
                                Argument<String> modifierNameArgument,
                                Argument<Operation> modifierConditionTypeArgument,
                                Argument<Number> modifierValueArgument) implements PlayerCondition {

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final String modifierName = modifierNameArgument.getValue(profile);
        final DrunkenModifier modifier = api.getModifierManager().getModifier(modifierName)
                .orElseThrow(() -> new QuestException("Unknown modifier %s".formatted(modifierName)));
        final Operation operation = modifierConditionTypeArgument.getValue(profile);
        final double modifierValue = modifierValueArgument.getValue(profile).doubleValue();
        if (modifierValue < modifier.minValue() || modifierValue > modifier.maxValue()) {
            throw new QuestException("%s level can only be between %s and %s".formatted(modifier.name(), modifier.minValue(), modifier.maxValue()));
        }
        final DrunkState drunkState = api.getDrunksManager().getDrunkState(profile.getPlayerUUID());
        final double actualValue;
        if (drunkState == null) {
            actualValue = modifier.minValue();
        } else {
            actualValue = drunkState.modifierValue(modifier);
        }
        return operation.check(actualValue, modifierValue);
    }
}
