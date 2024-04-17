package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.util.Locale;

/**
 * Represents an objective that is completed when the status of the received resource pack matches the target status.
 */
public class ResourcePackObjective extends Objective implements Listener {

    /**
     * The target status for the received resource pack.
     */
    private final VariableString targetStatus;

    /**
     * Constructs a new ResourcePackObjective instance.
     *
     * @param instruction The instruction object.
     * @throws InstructionParseException if an error occurs while parsing the instruction.
     */
    public ResourcePackObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        targetStatus = new VariableString(instruction.getPackage(), instruction.next());
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    /**
     * Handles the PlayerResourcePackStatusEvent event.
     *
     * @param event The event object.
     */
    @EventHandler
    public void onResourcePackReceived(final PlayerResourcePackStatusEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
            final PlayerResourcePackStatusEvent.Status status = PlayerResourcePackStatusEvent.Status.valueOf(targetStatus.getString(onlineProfile).toUpperCase(Locale.ROOT));
            if (status.equals(event.getStatus())) {
                completeObjective(onlineProfile);
            }
        }
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
