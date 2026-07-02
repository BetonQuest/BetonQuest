package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.bukkit.api.TheBrewingProjectApi;
import dev.jsinco.brewery.bukkit.api.event.transaction.BarrelExtractEvent;
import dev.jsinco.brewery.bukkit.api.event.transaction.BarrelInsertEvent;
import dev.jsinco.brewery.bukkit.api.event.transaction.CauldronExtractEvent;
import dev.jsinco.brewery.bukkit.api.event.transaction.CauldronInsertEvent;
import dev.jsinco.brewery.bukkit.api.event.transaction.DistilleryExtractEvent;
import dev.jsinco.brewery.bukkit.api.event.transaction.DistilleryInsertEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.BrewQualityArgument;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.BrewingStructureType;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.TransferType;

/**
 * Factory for {@link BrewTransferObjective}.
 *
 * @param api the TheBrewingProject API
 */
public record BrewTransferObjectiveFactory(TheBrewingProjectApi api) implements ObjectiveFactory {

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<TransferType> transferTypeArgument = instruction.enumeration(TransferType.class).get();
        final Argument<BrewingStructureType> structureTypeArgument = instruction.enumeration(BrewingStructureType.class).get();
        final Argument<String> brewType = instruction.string().get();
        final BrewQualityArgument brewQualityArgument = BrewQualityArgument.parseInstructions(instruction);
        final BrewTransferObjective objective = new BrewTransferObjective(
                service,
                api.getBrewManager(),
                brewQualityArgument,
                brewType,
                transferTypeArgument,
                structureTypeArgument
        );
        service.request(DistilleryExtractEvent.class)
                .onlineHandler(objective::handleExtract)
                .player(DistilleryExtractEvent::getPlayer)
                .subscribe(true);
        service.request(BarrelExtractEvent.class)
                .onlineHandler(objective::handleExtract)
                .player(BarrelExtractEvent::getPlayer)
                .subscribe(true);
        service.request(CauldronExtractEvent.class)
                .onlineHandler(objective::handleExtract)
                .player(CauldronExtractEvent::getPlayer)
                .subscribe(true);
        service.request(DistilleryInsertEvent.class)
                .onlineHandler(objective::handleInsert)
                .player(DistilleryInsertEvent::getPlayer)
                .subscribe(true);
        service.request(BarrelInsertEvent.class)
                .onlineHandler(objective::handleInsert)
                .player(BarrelInsertEvent::getPlayer)
                .subscribe(true);
        service.request(CauldronInsertEvent.class)
                .onlineHandler(objective::handleInsert)
                .player(CauldronInsertEvent::getPlayer)
                .subscribe(true);
        return objective;
    }
}
