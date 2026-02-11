package org.betonquest.betonquest.api.quest.npc;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.entity.Player;

/**
 * Hides (or shows) Npcs based on conditions defined in the {@code hide_npcs} section of a {@link QuestPackage}.
 */
public interface NpcHider {

    /**
     * Checks if the Npc should be invisible to the player.
     * <p>
     * This is primary used to cancel Npc spawn events.
     *
     * @param npcId   the id of the Npc
     * @param profile the profile to check conditions for
     * @return if the npc is stored and the hide conditions are met
     */
    boolean isHidden(NpcIdentifier npcId, OnlineProfile profile);

    /**
     * Allows to check if a Npc should be hidden.
     *
     * @param npc    the Npc to check
     * @param player the player to check conditions with
     * @return if the Npc is hidden with a Npc Hider
     */
    boolean isHidden(Npc<?> npc, Player player);
}
