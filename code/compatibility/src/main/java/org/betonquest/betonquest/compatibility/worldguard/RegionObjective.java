package org.betonquest.betonquest.compatibility.worldguard;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.quest.objective.location.AbstractLocationObjective;
import org.bukkit.Location;

/**
 * The region objective requires the player to be inside a specific region.
 */
public class RegionObjective extends AbstractLocationObjective {
    /**
     * The name of the region.
     */
    private final Variable<String> name;

    /**
     * The constructor takes an Instruction object as a parameter and throws an QuestException.
     *
     * @param instruction the Instruction object to be used in the constructor
     * @param name        the name of the region
     * @throws QuestException if there is an error while parsing the instruction
     */
    public RegionObjective(final Instruction instruction, final Variable<String> name) throws QuestException {
        super(instruction);
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
