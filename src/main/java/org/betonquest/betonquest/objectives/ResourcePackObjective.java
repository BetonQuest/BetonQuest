package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableString;
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
     * @throws QuestException if an error occurs while parsing the instruction.
     */
    public ResourcePackObjective(final Instruction instruction) throws QuestException {
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
        processObjective(onlineProfile, event.getStatus());
    }

    /**
     * Processes the objective. This is needed if the resource pack is received before the objective is started.
     * If a plugin handles the resource pack normally the PlayerResourcePackStatusEvent
     * is called after the objective is started.
     *
     * @param onlineProfile The online profile.
     * @param status        The status of the received resource pack.
     */
    public void processObjective(final OnlineProfile onlineProfile, final PlayerResourcePackStatusEvent.Status status) {
        if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
            final PlayerResourcePackStatusEvent.Status expectedStatus = PlayerResourcePackStatusEvent.Status.valueOf(targetStatus.getString(onlineProfile).toUpperCase(Locale.ROOT));
            if (expectedStatus.equals(status)) {
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
