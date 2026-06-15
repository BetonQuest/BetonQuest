package org.betonquest.betonquest.compatibility.thebrewingproject.objective;

import dev.jsinco.brewery.api.structure.StructureType;
import dev.jsinco.brewery.bukkit.api.event.structure.BarrelDestroyEvent;
import dev.jsinco.brewery.bukkit.api.event.structure.DistilleryDestroyEvent;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;

/**
 * The objective reached whenever a TheBrewingProject structure is destroyed.
 *
 * @param structureTypeArgument An argument to specify structure type
 * @param service               The objective service
 */
public record StructureDestroyObjective(
        @SuppressWarnings("rawtypes") Argument<StructureType> structureTypeArgument,
        ObjectiveService service) implements Objective {

    /**
     * Handle barrel destroy event.
     *
     * @param ignoredEvent  An ignored barrel destroy event
     * @param onlineProfile The player profile
     * @throws QuestException If an argument is invalid
     */
    public void handleBarrelDestroy(final BarrelDestroyEvent ignoredEvent, final OnlineProfile onlineProfile) throws QuestException {
        final StructureType<?> structureType = structureTypeArgument.getValue(onlineProfile);
        if (structureType == StructureType.BARREL) {
            service.complete(onlineProfile);
        }
    }

    /**
     * Handle distillery destroy event.
     *
     * @param ignoredEvent  An ignored distillery destroy event
     * @param onlineProfile The player profile
     * @throws QuestException If an argument is invalid
     */
    public void handleDistilleryDestroy(final DistilleryDestroyEvent ignoredEvent, final OnlineProfile onlineProfile) throws QuestException {
        final StructureType<?> structureType = structureTypeArgument.getValue(onlineProfile);
        if (structureType == StructureType.DISTILLERY) {
            service.complete(onlineProfile);
        }
    }

    @Override
    public ObjectiveService getService() {
        return service;
    }
}
