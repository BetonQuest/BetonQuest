package org.betonquest.betonquest.compatibility.thebrewingproject.item;

import dev.jsinco.brewery.api.brew.BrewQuality;
import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.quest.TypeFactory;

/**
 * Factory for {@link BrewItemWrapper}.
 *
 * @param api the TheBrewingProject API
 */
public record BrewItemFactory(TheBrewingProjectApi api) implements TypeFactory<QuestItemWrapper> {

    @Override
    public QuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<String> recipeNameArgument = instruction.string().get();
        final FlagArgument<BrewQuality> brewQualityArgument = instruction.enumeration(BrewQuality.class).getFlag("quality", BrewQuality.EXCELLENT);
        return new BrewItemWrapper(api, recipeNameArgument, brewQualityArgument);
    }
}
