package org.betonquest.betonquest.compatibility.worldguard;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.quest.objective.location.AbstractLocationObjective;
import org.bukkit.Location;

/**
 * The region objective requires the player to be inside a specific region.
 */
public class RegionObjective extends AbstractLocationObjective {

    /**
     * The name of the region.
     */
    private final Argument<String> name;

    /**
     * The constructor takes an Instruction object as a parameter and throws an QuestException.
     *
     * @param service the ObjectiveFactoryService to be used in the constructor
     * @param name    the name of the region
     * @param entry   the entry flag for the region objective
     * @param exit    the exit flag for the region objective
     * @throws QuestException if there is an error while parsing the instruction
     */
    public RegionObjective(final ObjectiveService service, final Argument<String> name,
                           final FlagArgument<Boolean> entry, final FlagArgument<Boolean> exit) throws QuestException {
        super(service, entry, exit);
        this.name = name;
    }

    @Override
    protected boolean isInside(final OnlineProfile onlineProfile, final Location location) throws QuestException {
        return WorldGuardIntegrator.isInsideRegion(location, name.getValue(onlineProfile));
    }
}
