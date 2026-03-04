package org.betonquest.betonquest.compatibility.luckperms;

import net.luckperms.api.context.ContextCalculator;
import org.betonquest.betonquest.api.data.Persistence;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Provides all per-player tags and all global tags as LuckPerms
 * contexts.
 */
public final class TagCalculatorUtils {

    /**
     * The BetonQuest tag.
     */
    public static final String KEY_LOCAL = "betonquest:tag:";

    /**
     * The global BetonQuest tag.
     */
    public static final String KEY_GLOBAL = "betonquest:globaltag:";

    private TagCalculatorUtils() {
    }

    /**
     * Get an anonymous ContextCalculator. It has to be anonymous to prevent the loading of the class when no LP is installed.
     *
     * @param persistence     the persistence instance
     * @param profileProvider the profile provider instance
     * @return a ContextCalculator
     */
    public static ContextCalculator<Player> getTagContextCalculator(final Persistence persistence, final ProfileProvider profileProvider) {
        return (player, contextConsumer) -> {
            if (player.isOnline()) {
                final OnlineProfile onlineProfile = profileProvider.getProfile(player);
                persistence.profile(onlineProfile).tags().get().forEach(tag -> contextConsumer.accept(KEY_LOCAL + tag, "true"));
            }
            final Set<String> globalDataTags = persistence.global().tags().get();
            globalDataTags.forEach(tag -> contextConsumer.accept(KEY_GLOBAL + tag, "true"));
        };
    }
}
