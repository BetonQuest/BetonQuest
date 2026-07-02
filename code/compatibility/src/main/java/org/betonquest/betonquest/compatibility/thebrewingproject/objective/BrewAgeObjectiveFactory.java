package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.api.breweries.BarrelType;
import dev.jsinco.brewery.api.util.BreweryRegistry;
import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import dev.jsinco.brewery.bukkit.api.event.transaction.BarrelExtractEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.BreweryKeyedParser;

/**
 * Factory for {@link BrewAgeObjective}.
 *
 * @param api the TheBrewingProject api
 */
public record BrewAgeObjectiveFactory(TheBrewingProjectApi api) implements ObjectiveFactory {

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<BarrelType> barrelTypeArgument = instruction.parse(new BreweryKeyedParser<>(BreweryRegistry.BARREL_TYPE)).get();
        final Argument<Number> ageTimeArgument = instruction.number().atLeast(0.5).get();
        final BrewAgeObjective objective = new BrewAgeObjective(
                api.getConfiguration().barrels().agingYearTicks(),
                api.getBrewManager(),
                service,
                barrelTypeArgument,
                ageTimeArgument
        );
        service.request(BarrelExtractEvent.class)
                .onlineHandler(objective::handle)
                .player(BarrelExtractEvent::getPlayer)
                .subscribe(true);
        return objective;
    }
}
