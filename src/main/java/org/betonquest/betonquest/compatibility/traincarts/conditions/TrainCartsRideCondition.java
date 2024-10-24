package org.betonquest.betonquest.compatibility.traincarts.conditions;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.compatibility.traincarts.TrainCartsUtils;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * The TrainCarts ride condition checks if a player is currently riding a train from TrainCarts.
 * It returns {@code true} if the player is riding the train with the specified name, otherwise {@code false}.
 */
public class TrainCartsRideCondition implements OnlineCondition {
    /**
     * The name of the train.
     */
    private final String trainName;

    /**
     * Create the TrainCarts ride condition.
     *
     * @param trainName the name of the train
     */
    public TrainCartsRideCondition(final String trainName) {
        this.trainName = trainName;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestRuntimeException {
        return TrainCartsUtils.isRidingTrainCart(profile, trainName);
    }
}
