package org.betonquest.betonquest.compatibility.traincarts;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for TrainCarts.
 */
public final class TrainCartsUtils {

    /**
     * This class cannot be instantiated.
     */
    private TrainCartsUtils() {
    }

    /**
     * Checks if a profile is currently riding a train from TrainCarts with the specified name.
     *
     * @param profile   the {@link OnlineProfile} to check.
     * @param trainName the name of the train.
     * @return {@code true} if the player is riding the train with the specified name, otherwise {@code false}.
     */
    public static boolean isRidingTrainCart(final OnlineProfile profile, final String trainName) {
        final MinecartMember<?> minecartMember = getMinecartMember(profile);
        if (minecartMember == null) {
            return false;
        }

        final String groupName = minecartMember.getGroup().getProperties().getTrainName();
        return trainName.equals(groupName);
    }

    /**
     * Checks if a profile is currently riding a train from TrainCarts.
     *
     * @param profile the {@link OnlineProfile} to check.
     * @return {@code true} if the player is riding a train, otherwise {@code false}.
     */
    public static boolean isRidingTrainCart(final OnlineProfile profile) {
        return getMinecartMember(profile) != null;
    }

    /**
     * Gets the {@link MinecartMember} the player is currently riding.
     *
     * @param profile the {@link OnlineProfile} to get the {@link MinecartMember} from.
     * @return the {@link MinecartMember} the player is currently riding or else {@code null}.
     */
    @Nullable
    public static MinecartMember<?> getMinecartMember(final OnlineProfile profile) {
        final Entity vehicle = profile.getPlayer().getVehicle();
        if (vehicle == null) {
            return null;
        }
        return MinecartMemberStore.getFromEntity(vehicle);
    }

    /**
     * Checks if the train Name matches with the name from the instruction.
     *
     * @param name      the name of the train from the instruction.
     * @param trainName the name of the train to check.
     * @return {@code true} if the train name matches with the name from the instruction, otherwise {@code false}.
     */
    public static boolean isValidTrain(final String name, final String trainName) {
        return name.isEmpty() || name.equals(trainName);
    }
}
