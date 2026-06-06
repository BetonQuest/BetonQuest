package org.betonquest.betonquest.compatibility.tbp.item;

import dev.jsinco.brewery.api.brew.BrewQuality;
import dev.jsinco.brewery.api.recipe.Recipe;
import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.quest.TypeFactory;
import org.bukkit.inventory.ItemStack;

public class TbpItemFactory implements TypeFactory<QuestItemWrapper> {

    private final TheBrewingProjectApi api;

    public TbpItemFactory(final TheBrewingProjectApi api) {
        this.api = api;
    }

    @Override
    public QuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<String> recipeNameArgument = instruction.string().get();
        final Argument<BrewQuality> brewQualityArgument = instruction.enumeration(BrewQuality.class).get();
        return profile -> {
            final String recipeName = recipeNameArgument.getValue(profile);
            final BrewQuality brewQuality = brewQualityArgument.getValue(profile);
            final Recipe<ItemStack> recipe = api.getRecipeRegistry().getRecipe(recipeName)
                    .orElseThrow(() -> new QuestException(String.format("Unknown recipe: %s", recipeName)));
            return new TbpBrewItem(recipe, recipeName, brewQuality, api.getBrewManager());
        };
    }
}
