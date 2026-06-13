package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.api.structure.StructureType;
import dev.jsinco.brewery.api.util.BreweryRegistry;
import dev.jsinco.brewery.bukkit.api.event.structure.BarrelDestroyEvent;
import dev.jsinco.brewery.bukkit.api.event.structure.DistilleryDestroyEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.BreweryKeyedParser;

/**
 * Factory for {@link StructureDestroyObjective}.
 */
public record StructureDestroyObjectiveFactory() implements ObjectiveFactory {

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        @SuppressWarnings("rawtypes") final Argument<StructureType> structureTypeArgument = instruction.parse(new BreweryKeyedParser<>(BreweryRegistry.STRUCTURE_TYPE))
                .get();
        final StructureDestroyObjective objective = new StructureDestroyObjective(structureTypeArgument, service);
        service.request(DistilleryDestroyEvent.class)
                .onlineHandler(objective::handleDistilleryDestroy)
                .player(DistilleryDestroyEvent::getPlayer)
                .subscribe(true);
        service.request(BarrelDestroyEvent.class)
                .onlineHandler(objective::handleBarrelDestroy)
                .player(BarrelDestroyEvent::getPlayer)
                .subscribe(true);
        return objective;
    }
}
