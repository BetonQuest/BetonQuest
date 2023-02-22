package org.betonquest.betonquest.quest.event.worldselector;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * World selector that selects the world that the player is inside.
 */
public class PlayerWorldSelector implements WorldSelector {
    /**
     * Create a selector that will select the world a player is in.
     */
    public PlayerWorldSelector() {
    }

    @Override
    public World getWorld(final Profile profile) throws QuestRuntimeException {
        return profile.getOnlineProfile()
                .map(OnlineProfile::getPlayer)
                .map(Player::getWorld)
                .orElseThrow(() -> new QuestRuntimeException("Player must be online."));
    }
}
