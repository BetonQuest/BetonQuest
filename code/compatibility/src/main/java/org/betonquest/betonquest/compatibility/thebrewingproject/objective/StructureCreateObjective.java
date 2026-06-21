package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.bukkit.api.event.structure.BarrelCreateEvent;
import dev.jsinco.brewery.bukkit.api.event.structure.CauldronCreateEvent;
import dev.jsinco.brewery.bukkit.api.event.structure.DistilleryCreateEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.BrewingStructureType;

/**
 * Objective for creating structures.
 *
 * @param structureTypeArgument A structure type argument
 * @param service               The objective service
 */
public record StructureCreateObjective(
        Argument<BrewingStructureType> structureTypeArgument,
        ObjectiveService service) implements Objective {

    /**
     * Handle barrel create event.
     *
     * @param ignoredEvent  An ignored barrel create event
     * @param onlineProfile The player profile
     * @throws QuestException If an argument is invalid
     */
    public void handleBarrelCreate(final BarrelCreateEvent ignoredEvent, final OnlineProfile onlineProfile) throws QuestException {
        final BrewingStructureType structureType = structureTypeArgument.getValue(onlineProfile);
        if (structureType == BrewingStructureType.BARREL) {
            service.complete(onlineProfile);
        }
    }

    /**
     * Handle distillery create event.
     *
     * @param ignoredEvent  An ignored distillery create event
     * @param onlineProfile The player profile
     * @throws QuestException If an argument is invalid
     */
    public void handleDistilleryCreate(final DistilleryCreateEvent ignoredEvent, final OnlineProfile onlineProfile) throws QuestException {
        final BrewingStructureType structureType = structureTypeArgument.getValue(onlineProfile);
        if (structureType == BrewingStructureType.DISTILLERY) {
            service.complete(onlineProfile);
        }
    }

    /**
     * Handle cauldron create event.
     *
     * @param ignoredEvent  An ignored cauldron create event
     * @param onlineProfile The player profile
     * @throws QuestException IF an argument is invalid
     */
    public void handleCauldronCreate(final CauldronCreateEvent ignoredEvent, final OnlineProfile onlineProfile) throws QuestException {
        final BrewingStructureType structureType = structureTypeArgument.getValue(onlineProfile);
        if (structureType == BrewingStructureType.CAULDRON) {
            service.complete(onlineProfile);
        }
    }

    @Override
    public ObjectiveService getService() {
        return service;
    }
}
