package org.betonquest.betonquest.compatibility.itemsadder.objective;

import dev.lone.itemsadder.api.CustomStack;
import org.betonquest.betonquest.api.CountingObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;

/**
 * Checks for interaction with a {@link dev.lone.itemsadder.api.CustomBlock CustomBlock}.
 */
public class IABlockObjective extends CountingObjective {

    /**
     * Item of block to check.
     */
    private final Argument<CustomStack> itemID;

    /**
     * Create an ItemsAdder block objective.
     *
     * @param service           the objective service
     * @param targetAmount      the target amount of units required for completion
     * @param notifyMessageName the message name used for notifying by default
     * @param itemID            the item of the block to check
     * @throws QuestException if the syntax is wrong or any error happens while parsing
     */
    public IABlockObjective(final ObjectiveService service, final Argument<Number> targetAmount, final String notifyMessageName,
                            final Argument<CustomStack> itemID) throws QuestException {
        super(service, targetAmount, notifyMessageName);
        this.itemID = itemID;
    }

    /**
     * Progress the objective with the placed block's id and profile who placed.
     *
     * @param namespacedID the name of the involved block
     * @param profile      the involved profile
     * @throws QuestException when arguments could not be resolved
     */
    public void handle(final String namespacedID, final OnlineProfile profile) throws QuestException {
        if (itemID.getValue(profile).getNamespacedID().equals(namespacedID)) {
            getCountingData(profile).progress();
            completeIfDoneOrNotify(profile);
        }
    }
}
