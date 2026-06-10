package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.manager.data.OfflinePlayerData;
import org.betonquest.betonquest.api.profile.Profile;

/**
 * Utility methods for MMOCore.
 */
public final class MMOCoreUtils {

    /**
     * Utility classes shouldn't be instantiated.
     */
    private MMOCoreUtils() {
    }

    /**
     * Gets the {@link OfflinePlayerData} from MMOCore for a profile.
     *
     * @param profile the profile
     * @return the player data
     */
    public static OfflinePlayerData getOfflinePlayerData(final Profile profile) {
        return MMOCore.plugin.playerDataManager.getOffline(profile.getPlayerUUID());
    }
}
