package org.betonquest.betonquest.quest.objective.data;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.bukkit.event.PlayerObjectiveChangeEvent;
import org.betonquest.betonquest.api.bukkit.event.PlayerTagAddEvent;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveState;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Player needs to get a certain tag.
 */
public class TagObjective extends Objective implements Listener {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * Tag to get.
     */
    private final Variable<String> tag;

    /**
     * Creates a new tag objective.
     *
     * @param instruction       Instruction object representing the objective
     * @param playerDataStorage the storage for player data
     * @param tag               the tag to get
     * @throws QuestException if the syntax is wrong or any error happens while parsing
     */
    public TagObjective(final Instruction instruction, final PlayerDataStorage playerDataStorage, final Variable<String> tag) throws QuestException {
        super(instruction);
        this.playerDataStorage = playerDataStorage;
        this.tag = tag;
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) throws QuestException {
        return tag.getValue(profile);
    }

    /**
     * Handles tag adding.
     *
     * @param event the event to listen
     */
    @EventHandler
    public void onTag(final PlayerTagAddEvent event) {
        qeHandler.handle(() -> {
            if (containsPlayer(event.getProfile())
                    && event.getTag().equals(tag.getValue(event.getProfile()))
                    && checkConditions(event.getProfile())) {
                completeObjective(event.getProfile());
            }
        });
    }

    /**
     * Checks for objective completion when it is started.
     *
     * @param event the event to listen
     */
    @EventHandler
    public void onStart(final PlayerObjectiveChangeEvent event) {
        if (event.getState() != ObjectiveState.ACTIVE || !containsPlayer(event.getProfile())) {
            return;
        }
        qeHandler.handle(() -> {
            if (playerDataStorage.getOffline(event.getProfile()).hasTag(tag.getValue(event.getProfile()))
                    && checkConditions(event.getProfile())) {
                completeObjective(event.getProfile());
            }
        });
    }
}
