package org.betonquest.betonquest.lib.instruction.argument;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

/**
 * Defines commonly used argument implementations for profiles not based on instructions.
 */
public final class DefaultArguments {

    /**
     * Argument for an online player's location.
     */
    public static final Argument<Location> PLAYER_LOCATION = profile -> online(profile, "location").getPlayer().getLocation();

    /**
     * Argument for an online player's world.
     */
    public static final Argument<World> PLAYER_WORLD = profile -> online(profile, "world").getPlayer().getWorld();

    private DefaultArguments() {
    }

    /**
     * Gets the online profile from a profile.
     * This is used to safely get the online profile inside the contracts of an argument.
     * <br>
     * It checks for null and online presence and will throw if the profile is not an online one.
     *
     * @param profile the profile to get the online profile from
     * @param name    the readable name of the object you want to get from the profile used in the exception messages
     * @return the online profile
     * @throws QuestException if there is no online profile
     */
    public static OnlineProfile online(@Nullable final Profile profile, final String name) throws QuestException {
        if (profile == null) {
            throw new QuestException("Can't get '%s' for null profile".formatted(name));
        }
        return profile.getOnlineProfile()
                .orElseThrow(() -> new QuestException("Can't get '%s' for offline profile".formatted(name)));
    }
}
