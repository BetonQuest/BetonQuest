package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.bukkit.api.event.structure.BarrelDestroyEvent;
import dev.jsinco.brewery.bukkit.api.event.structure.CauldronDestroyEvent;
import dev.jsinco.brewery.bukkit.api.event.structure.DistilleryDestroyEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.BrewingStructureType;

/**
 * Factory for {@link StructureDestroyObjective}.
 */
public record StructureDestroyObjectiveFactory() implements ObjectiveFactory {

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<BrewingStructureType> structureTypeArgument = instruction.enumeration(BrewingStructureType.class)
                .get();
        final StructureDestroyObjective objective = new StructureDestroyObjective(service, structureTypeArgument);
        service.request(DistilleryDestroyEvent.class)
                .onlineHandler(objective::handleDistilleryDestroy)
                .player(DistilleryDestroyEvent::getPlayer)
                .subscribe(true);
        service.request(BarrelDestroyEvent.class)
                .onlineHandler(objective::handleBarrelDestroy)
                .player(BarrelDestroyEvent::getPlayer)
                .subscribe(true);
        service.request(CauldronDestroyEvent.class)
                .onlineHandler(objective::handleCauldronDestroy)
                .player(CauldronDestroyEvent::getPlayer)
                .subscribe(true);
        return objective;
    }
}
