package org.betonquest.betonquest.quest.objective.resourcepack;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

/**
 * Represents an objective that is completed when the status of the received resource pack matches the target status.
 */
public class ResourcepackObjective extends Objective implements Listener {

    /**
     * The target status for the received resource pack.
     */
    private final Variable<PlayerResourcePackStatusEvent.Status> targetStatus;

    /**
     * Constructs a new ResourcepackObjective instance.
     *
     * @param instruction  The instruction object.
     * @param targetStatus The target status for the received resource pack.
     * @throws QuestException if an error occurs while parsing the instruction.
     */
    public ResourcepackObjective(final Instruction instruction, final Variable<PlayerResourcePackStatusEvent.Status> targetStatus) throws QuestException {
        super(instruction);
        this.targetStatus = targetStatus;
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
        processObjective(profileProvider.getProfile(event.getPlayer()), event.getStatus());
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
            qeHandler.handle(() -> {
                final PlayerResourcePackStatusEvent.Status expectedStatus = targetStatus.getValue(onlineProfile);
                if (expectedStatus.equals(status)) {
                    completeObjective(onlineProfile);
                }
            });
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
