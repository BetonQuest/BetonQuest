package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.api.brew.Brew;
import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import dev.jsinco.brewery.bukkit.api.event.transaction.BarrelExtractEvent;
import dev.jsinco.brewery.bukkit.api.event.transaction.CauldronExtractEvent;
import dev.jsinco.brewery.bukkit.api.event.transaction.DistilleryExtractEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.AgeArgument;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.CookArgument;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.DistillArgument;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.MixArgument;

import java.util.Locale;

/**
 * Factory for {@link BrewCreateObjective}.
 *
 * @param api TheBrewingProject API
 */
public record BrewCreateObjectiveFactory(TheBrewingProjectApi api) implements ObjectiveFactory {

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final String processStepArgument = instruction.nextElement().toLowerCase(Locale.ROOT);
        final Argument<QuestFunction<Brew, Boolean>> brewPredicateArgument = switch (processStepArgument) {
            case "distill" -> DistillArgument.fromChain(instruction);
            case "age" -> AgeArgument.fromChain(instruction, api.getConfiguration().barrels().agingYearTicks());
            case "cook" -> CookArgument.fromChain(instruction, api.getIngredientManager());
            case "mix" -> MixArgument.fromChain(instruction, api.getIngredientManager());
            default -> throw new QuestException("Unknown argument '%s'".formatted(processStepArgument));
        };
        final BrewCreateObjective objective = new BrewCreateObjective(brewPredicateArgument, service, api.getBrewManager());
        service.request(DistilleryExtractEvent.class)
                .onlineHandler(objective::handle)
                .player(DistilleryExtractEvent::getPlayer)
                .subscribe(true);
        service.request(BarrelExtractEvent.class)
                .onlineHandler(objective::handle)
                .player(BarrelExtractEvent::getPlayer)
                .subscribe(true);
        service.request(CauldronExtractEvent.class)
                .onlineHandler(objective::handle)
                .player(CauldronExtractEvent::getPlayer)
                .subscribe(true);
        return objective;
    }
}
