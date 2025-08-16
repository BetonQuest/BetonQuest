package org.betonquest.betonquest.kernel.processor.adapter;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Wrapper for player and playerless events.
 */
public class EventAdapter extends QuestAdapter<PlayerEvent, PlayerlessEvent> {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * QuestTypeApi to check conditions.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Instruction used to create the types.
     */
    private final Instruction instruction;

    /**
     * Conditions that must be met to execute.
     */
    private final Variable<List<ConditionID>> conditions;

    /**
     * Create a new Wrapper for variables with instruction.
     *
     * @param log          the custom logger for this class
     * @param questTypeApi the QuestTypeApi
     * @param instruction  the instruction used to create the types
     * @param player       the type requiring a profile for execution
     * @param playerless   the type working without a profile
     * @throws IllegalArgumentException if there is no type provided
     * @throws QuestException           when there was an error parsing conditions
     */
    public EventAdapter(final BetonQuestLogger log, final QuestTypeApi questTypeApi, final Instruction instruction, @Nullable final PlayerEvent player, @Nullable final PlayerlessEvent playerless) throws QuestException {
        super(instruction.getPackage(), player, playerless);
        this.log = log;
        this.questTypeApi = questTypeApi;
        this.instruction = instruction;
        conditions = instruction.getValueList("conditions", ConditionID::new);
    }

    /**
     * Fires an event for the profile if it meets the event's conditions.
     *
     * @param profile the {@link Profile} to execute for
     * @return whether the event was successfully handled or not
     * @throws QuestException if the event could not be executed or requires a profile for execution
     */
    public boolean fire(@Nullable final Profile profile) throws QuestException {
        if (player == null || profile == null) {
            return handleNullProfile();
        }
        log.debug(getPackage(), "Event will be fired for "
                + (profile.getOnlineProfile().isPresent() ? "online" : "offline") + " profile.");

        if (!questTypeApi.conditions(profile, conditions.getValue(profile))) {
            log.debug(getPackage(), "Event conditions were not met for " + profile);
            return false;
        }
        player.execute(profile);
        return true;
    }

    private boolean handleNullProfile() throws QuestException {
        if (playerless == null) {
            //throw new QuestException("Non-static event '" + instruction + "' cannot be executed without a profile reference!");
            log.warn(getPackage(), "Cannot execute non-static event '" + instruction.getID() + "' without a player!");
            return false;
        }
        log.debug(getPackage(), "Static event will be fired without a profile.");
        if (!questTypeApi.conditions(null, conditions.getValue(null))) {
            log.debug(getPackage(), "Event conditions were not met");
            return false;
        }
        playerless.execute();
        return true;
    }
}
