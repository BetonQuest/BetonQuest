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
 * @param service               the objective service
 * @param structureTypeArgument a structure type argument
 */
public record StructureCreateObjective(
        ObjectiveService service,
        Argument<BrewingStructureType> structureTypeArgument) implements Objective {

    /**
     * Handle barrel create event.
     *
     * @param ignoredEvent  an ignored barrel create event
     * @param onlineProfile the player profile
     * @throws QuestException if an argument is invalid
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
     * @param ignoredEvent  an ignored distillery create event
     * @param onlineProfile the player profile
     * @throws QuestException if an argument is invalid
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
     * @param ignoredEvent  an ignored cauldron create event
     * @param onlineProfile the player profile
     * @throws QuestException if an argument is invalid
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
