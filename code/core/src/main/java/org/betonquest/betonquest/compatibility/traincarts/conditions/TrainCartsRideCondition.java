package org.betonquest.betonquest.compatibility.traincarts.conditions;

import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.compatibility.traincarts.TrainCartsUtils;

/**
 * The TrainCarts ride condition checks if a player is currently riding a train from TrainCarts.
 * It returns {@code true} if the player is riding the train with the specified name, otherwise {@code false}.
 */
public class TrainCartsRideCondition implements OnlineCondition {
    /**
     * The name of the train.
     */
    private final Variable<String> trainName;

    /**
     * Create the TrainCarts ride condition.
     *
     * @param trainName the name of the train
     */
    public TrainCartsRideCondition(final Variable<String> trainName) {
        this.trainName = trainName;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        return TrainCartsUtils.isRidingTrainCart(profile, trainName.getValue(profile));
    }
}
