package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.bukkit.api.event.structure.BarrelDestroyEvent;
import dev.jsinco.brewery.bukkit.api.event.structure.CauldronDestroyEvent;
import dev.jsinco.brewery.bukkit.api.event.structure.DistilleryDestroyEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.compatibility.thebrewingproject.argument.BrewingStructureType;

/**
 * Objective for destroying structures.
 *
 * @param service               the objective service
 * @param structureTypeArgument an argument to specify structure type
 */
public record StructureDestroyObjective(ObjectiveService service,
                                        Argument<BrewingStructureType> structureTypeArgument) implements Objective {

    /**
     * Handle barrel destroy event.
     *
     * @param ignoredEvent  an ignored barrel destroy event
     * @param onlineProfile the player profile
     * @throws QuestException if an argument is invalid
     */
    public void handleBarrelDestroy(final BarrelDestroyEvent ignoredEvent, final OnlineProfile onlineProfile) throws QuestException {
        final BrewingStructureType structureType = structureTypeArgument.getValue(onlineProfile);
        if (structureType == BrewingStructureType.BARREL) {
            service.complete(onlineProfile);
        }
    }

    /**
     * Handle distillery destroy event.
     *
     * @param ignoredEvent  an ignored distillery destroy event
     * @param onlineProfile the player profile
     * @throws QuestException if an argument is invalid
     */
    public void handleDistilleryDestroy(final DistilleryDestroyEvent ignoredEvent, final OnlineProfile onlineProfile) throws QuestException {
        final BrewingStructureType structureType = structureTypeArgument.getValue(onlineProfile);
        if (structureType == BrewingStructureType.DISTILLERY) {
            service.complete(onlineProfile);
        }
    }

    /**
     * Handle cauldron destroy event.
     *
     * @param ignoredEvent  an ignored cauldron destroy event
     * @param onlineProfile the player profile
     * @throws QuestException iF an argument is invalid
     */
    public void handleCauldronDestroy(final CauldronDestroyEvent ignoredEvent, final OnlineProfile onlineProfile) throws QuestException {
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
