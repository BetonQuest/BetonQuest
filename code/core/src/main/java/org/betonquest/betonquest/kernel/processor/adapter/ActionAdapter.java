package org.betonquest.betonquest.kernel.processor.adapter;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Wrapper for player and playerless actions.
 */
public class ActionAdapter extends QuestAdapter<PlayerAction, PlayerlessAction> implements PrimaryThreadEnforceable {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Condition Manager to check conditions.
     */
    private final ConditionManager conditionManager;

    /**
     * Instruction used to create the types.
     */
    private final Identifier identifier;

    /**
     * Conditions that must be met to execute.
     */
    private final Argument<List<ConditionIdentifier>> conditions;

    /**
     * Create a new Wrapper for actions.
     *
     * @param log              the custom logger for this class
     * @param conditionManager the condition manager
     * @param identifier       the action's identifier
     * @param player           the type requiring a profile for execution
     * @param playerless       the type working without a profile
     * @param conditions       the conditions to check if the action should be executed
     * @throws IllegalArgumentException if there is no type provided
     */
    public ActionAdapter(final BetonQuestLogger log, final ConditionManager conditionManager, final Identifier identifier,
                         @Nullable final PlayerAction player, @Nullable final PlayerlessAction playerless,
                         final Argument<List<ConditionIdentifier>> conditions) {
        super(identifier.getPackage(), player, playerless);
        this.log = log;
        this.conditionManager = conditionManager;
        this.identifier = identifier;
        this.conditions = conditions;
    }

    /**
     * Fires an action for the profile if it meets the action's conditions.
     *
     * @param profile the {@link Profile} to execute for
     * @return whether the action was successfully handled or not
     * @throws QuestException if the action could not be executed
     */
    public boolean fire(@Nullable final Profile profile) throws QuestException {
        if (player == null || profile == null) {
            return handleNullProfile();
        }
        log.debug(getPackage(), "Action '%s'will be fired for %s profile.".formatted(identifier,
                profile.getOnlineProfile().isPresent() ? "online" : "offline"));

        if (!conditionManager.testAll(profile, conditions.getValue(profile))) {
            log.debug(getPackage(), "Action conditions were not met for " + profile);
            return false;
        }
        player.execute(profile);
        return true;
    }

    private boolean handleNullProfile() throws QuestException {
        if (playerless == null) {
            log.warn(getPackage(), "Cannot execute player-dependent action '%s' without a player!".formatted(identifier));
            return false;
        }
        log.debug(getPackage(), "Player-independent action will be fired without a profile.");
        if (!conditionManager.testAll(null, conditions.getValue(null))) {
            log.debug(getPackage(), "Action conditions were not met");
            return false;
        }
        playerless.execute();
        return true;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return player != null && player.isPrimaryThreadEnforced() || playerless != null && playerless.isPrimaryThreadEnforced();
    }
}
