package org.betonquest.betonquest.quest.objective.resourcepack;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

/**
 * Represents an objective that is completed when the status of the received resource pack matches the target status.
 */
public class ResourcepackObjective extends DefaultObjective {

    /**
     * The target status for the received resource pack.
     */
    private final Argument<PlayerResourcePackStatusEvent.Status> targetStatus;

    /**
     * Constructs a new ResourcepackObjective instance.
     *
     * @param service      the objective factory service.
     * @param targetStatus the target status for the received resource pack.
     * @throws QuestException if an error occurs while parsing the instruction.
     */
    public ResourcepackObjective(final ObjectiveFactoryService service, final Argument<PlayerResourcePackStatusEvent.Status> targetStatus) throws QuestException {
        super(service);
        this.targetStatus = targetStatus;
    }

    /**
     * Handles the PlayerResourcePackStatusEvent event.
     *
     * @param event         The event object.
     * @param onlineProfile The profile of the player that received the resource pack.
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onResourcePackReceived(final PlayerResourcePackStatusEvent event, final OnlineProfile onlineProfile) throws QuestException {
        processObjective(onlineProfile, event.getStatus());
    }

    /**
     * Processes the objective. This is needed if the resource pack is received before the objective is started.
     * If a plugin handles the resource pack normally the PlayerResourcePackStatusEvent
     * is called after the objective is started.
     *
     * @param onlineProfile The online profile.
     * @param status        The status of the received resource pack.
     * @throws QuestException if argument resolving for the profile fails
     */
    public void processObjective(final OnlineProfile onlineProfile, final PlayerResourcePackStatusEvent.Status status) throws QuestException {
        final PlayerResourcePackStatusEvent.Status expectedStatus = targetStatus.getValue(onlineProfile);
        if (expectedStatus == status) {
            getService().complete(onlineProfile);
        }
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
