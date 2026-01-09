package org.betonquest.betonquest.quest.objective.data;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.PlayerObjectiveChangeEvent;
import org.betonquest.betonquest.api.bukkit.event.PlayerTagAddEvent;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.ObjectiveState;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * Player needs to get a certain tag.
 */
public class TagObjective extends DefaultObjective {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * Tag to get.
     */
    private final Argument<String> tag;

    /**
     * Creates a new tag objective.
     *
     * @param service           the objective factory service
     * @param playerDataStorage the storage for player data
     * @param tag               the tag to get
     * @throws QuestException if the syntax is wrong or any error happens while parsing
     */
    public TagObjective(final ObjectiveFactoryService service, final PlayerDataStorage playerDataStorage, final Argument<String> tag) throws QuestException {
        super(service);
        this.playerDataStorage = playerDataStorage;
        this.tag = tag;
        service.getProperties().setProperty("name", tag::getValue);
    }

    /**
     * Handles tag adding.
     *
     * @param event   the event to listen
     * @param profile the profile which received the tag
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onTag(final PlayerTagAddEvent event, final Profile profile) throws QuestException {
        if (event.getTag().equals(tag.getValue(profile))) {
            getService().complete(profile);
        }
    }

    /**
     * Checks for objective completion when it is started.
     *
     * @param event   the event to listen
     * @param profile the profile which started the objective
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onStart(final PlayerObjectiveChangeEvent event, final Profile profile) throws QuestException {
        if (event.getState() != ObjectiveState.ACTIVE) {
            return;
        }
        if (playerDataStorage.getOffline(profile).hasTag(tag.getValue(profile))) {
            getService().complete(profile);
        }
    }
}
