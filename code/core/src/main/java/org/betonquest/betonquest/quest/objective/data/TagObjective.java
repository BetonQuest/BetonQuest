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
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) throws QuestException {
        return tag.getValue(profile);
    }

    /**
     * Handles tag adding.
     *
     * @param event   the event to listen
     * @param profile the profile which received the tag
     */
    public void onTag(final PlayerTagAddEvent event, final Profile profile) {
        qeHandler.handle(() -> {
            if (containsPlayer(profile)
                    && event.getTag().equals(tag.getValue(profile))
                    && checkConditions(profile)) {
                completeObjective(profile);
            }
        });
    }

    /**
     * Checks for objective completion when it is started.
     *
     * @param event   the event to listen
     * @param profile the profile which started the objective
     */
    public void onStart(final PlayerObjectiveChangeEvent event, final Profile profile) {
        if (event.getState() != ObjectiveState.ACTIVE || !containsPlayer(profile)) {
            return;
        }
        qeHandler.handle(() -> {
            if (playerDataStorage.getOffline(profile).hasTag(tag.getValue(profile))
                    && checkConditions(profile)) {
                completeObjective(profile);
            }
        });
    }
}
