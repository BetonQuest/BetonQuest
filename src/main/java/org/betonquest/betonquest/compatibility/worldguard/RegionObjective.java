package org.betonquest.betonquest.compatibility.worldguard;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.objectives.AbstractLocationObjective;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;

/**
 * The region objective requires the player to be inside a specific region.
 */
public class RegionObjective extends AbstractLocationObjective {
    /**
     * The name of the region.
     */
    private final VariableString name;

    /**
     * The constructor takes an Instruction object as a parameter and throws an QuestException.
     *
     * @param instruction the Instruction object to be used in the constructor
     * @throws QuestException if there is an error while parsing the instruction
     */
    public RegionObjective(final Instruction instruction) throws QuestException {
        super(BetonQuest.getInstance().getLoggerFactory().create(RegionObjective.class), instruction);
        name = new VariableString(instruction.getPackage(), instruction.next());
    }

    @Override
    protected boolean isInside(final OnlineProfile onlineProfile, final Location location) {
        return WorldGuardIntegrator.isInsideRegion(location, name.getString(onlineProfile));
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }

}
