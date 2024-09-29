package org.betonquest.betonquest.compatibility.traincarts;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

/**
 * Utility class for TrainCarts.
 */
public final class TrainCartsUtils {

    /**
     * This class cannot be instantiated.
     */
    private TrainCartsUtils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Checks if a profile is currently riding a train from TrainCarts with the specified name.
     *
     * @param profile   the {@link OnlineProfile} to check.
     * @param trainName the name of the train.
     * @return {@code true} if the player is riding the train with the specified name, otherwise {@code false}.
     */
    public static boolean ridesMatchingTrainName(final OnlineProfile profile, final String trainName) {
        final MinecartMember<?> minecartMember = getMinecartMember(profile);
        if (minecartMember == null) {
            return false;
        }

        final String groupName = minecartMember.getGroup().getProperties().getTrainName();
        return trainName.equalsIgnoreCase(groupName);
    }

    /**
     * Checks if a profile is currently riding a train from TrainCarts.
     *
     * @param profile the {@link OnlineProfile} to check.
     * @return {@code true} if the player is riding a train, otherwise {@code false}.
     */
    public static boolean ridesTrainCart(final OnlineProfile profile) {
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
}
