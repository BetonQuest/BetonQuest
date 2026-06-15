package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.api.breweries.CauldronType;
import dev.jsinco.brewery.api.util.BreweryRegistry;
import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import dev.jsinco.brewery.bukkit.api.event.transaction.CauldronExtractEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.BreweryKeyedParser;

import java.util.List;

/**
 * Factory for {@link BrewCookObjective}.
 *
 * @param api TheBrewingProject API
 */
public record BrewCookObjectiveFactory(TheBrewingProjectApi api) implements ObjectiveFactory {

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<CauldronType> cauldronTypeArgument = instruction.parse(new BreweryKeyedParser<>(BreweryRegistry.CAULDRON_TYPE)).get();
        final Argument<Number> cookTimeArgument = instruction.number().atLeast(0).get();
        final Argument<List<String>> ingredientsArgument = instruction.string().list().get();
        final BrewCookObjective objective = new BrewCookObjective(
                cauldronTypeArgument,
                cookTimeArgument,
                api.getIngredientManager().getIngredientsWithAmount(ingredientsArgument.getValue(null)),
                api.getBrewManager(),
                service
        );
        service.request(CauldronExtractEvent.class)
                .onlineHandler(objective::handle)
                .player(CauldronExtractEvent::getPlayer)
                .subscribe(true);
        return objective;
    }
}
