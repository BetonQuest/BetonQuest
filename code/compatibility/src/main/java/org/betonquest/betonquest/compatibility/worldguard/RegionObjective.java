package org.betonquest.betonquest.compatibility.worldguard;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
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
     * @throws QuestException if there is an error while parsing the instruction
     */
    public RegionObjective(final ObjectiveFactoryService service, final Argument<String> name) throws QuestException {
        super(service);
        this.name = name;
    }

    @Override
    protected boolean isInside(final OnlineProfile onlineProfile, final Location location) throws QuestException {
        return WorldGuardIntegrator.isInsideRegion(location, name.getValue(onlineProfile));
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
