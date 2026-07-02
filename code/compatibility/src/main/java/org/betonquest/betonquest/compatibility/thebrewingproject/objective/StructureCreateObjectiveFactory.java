package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.bukkit.api.event.structure.BarrelCreateEvent;
import dev.jsinco.brewery.bukkit.api.event.structure.CauldronCreateEvent;
import dev.jsinco.brewery.bukkit.api.event.structure.DistilleryCreateEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.BrewingStructureType;

/**
 * Factory for {@link StructureCreateObjective}.
 */
public record StructureCreateObjectiveFactory() implements ObjectiveFactory {

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<BrewingStructureType> structureTypeArgument = instruction.enumeration(BrewingStructureType.class)
                .get();
        final StructureCreateObjective objective = new StructureCreateObjective(
                service, structureTypeArgument
        );
        service.request(BarrelCreateEvent.class)
                .onlineHandler(objective::handleBarrelCreate)
                .player(BarrelCreateEvent::getPlayer)
                .subscribe(true);
        service.request(DistilleryCreateEvent.class)
                .onlineHandler(objective::handleDistilleryCreate)
                .player(DistilleryCreateEvent::getPlayer)
                .subscribe(true);
        service.request(CauldronCreateEvent.class)
                .onlineHandler(objective::handleCauldronCreate)
                .player(CauldronCreateEvent::getPlayer)
                .subscribe(true);
        return objective;
    }
}
