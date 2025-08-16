package org.betonquest.betonquest.quest.objective.logout;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Requires the player to leave the server.
 */
public class LogoutObjective extends Objective implements Listener {

    /**
     * Constructor for the LogoutObjective.
     *
     * @param instruction the instruction that created this objective
     * @throws QuestException if there is an error in the instruction
     */
    public LogoutObjective(final Instruction instruction) throws QuestException {
        super(instruction);
    }

    /**
     * Check if the player has left the server.
     *
     * @param event the event that triggered this method
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onQuit(final PlayerQuitEvent event) {
        final OnlineProfile onlineProfile = profileProvider.getProfile(event.getPlayer());
        if (containsPlayer(onlineProfile) && checkConditions(onlineProfile)) {
            completeObjective(onlineProfile);
        }
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
