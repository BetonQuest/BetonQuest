package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * The NpcManager is responsible for handling NPCs known to BetonQuest.
 * <br> <br>
 * Each npc is identified by a {@link NpcIdentifier} which consists of the user-defined name in the configuration
 * as well as the {@link QuestPackage} the npc belongs to.
 */
public interface NpcManager {

    /**
     * Obtains an {@link Npc} by its {@link NpcIdentifier} with an unknown original type.
     * <br> <br>
     * The specified {@link Profile} will be used to resolve any placeholders in the npc's instruction.
     * <br> <br>
     * If no profile is specified, the npc will be resolved without any profile and any related placeholders
     * will be resolved without a profile.
     * If there are placeholders requiring a profile, but none is given, the resolution will fail.
     *
     * @param profile       the profile to resolve the npc for or null if no profile is involved
     * @param npcIdentifier the identifier of the npc
     * @return the npc for the given identifier
     * @throws QuestException if there is no npc with the given identifier
     */
    Npc<?> get(@Nullable Profile profile, NpcIdentifier npcIdentifier) throws QuestException;

    /**
     * Checks if the npc of the specified {@link NpcIdentifier} is supposed to be hidden
     * for the specified {@link OnlineProfile}.
     * <br> <br>
     * The check involves the evaluation of all conditions as defined in the {@code hide_npcs} section
     * of the related {@link QuestPackage}.
     * <br> <br>
     * This may be used to cancel npc spawn events.
     *
     * @param npcId   the id of the npc
     * @param profile the profile to check conditions for
     * @return if the npc is supposed to be hidden
     * @see #isHidden(Npc, Player)
     * @see Npc#hide(OnlineProfile)
     * @see Npc#show(OnlineProfile)
     */
    boolean isHidden(NpcIdentifier npcId, OnlineProfile profile);

    /**
     * Checks if the specified {@link Npc} is supposed to be hidden for the specified {@link Player}.
     * <br> <br>
     * The check involves the evaluation of all conditions as defined in the {@code hide_npcs} section
     * of the related {@link QuestPackage}.
     * <br> <br>
     * This may be used to cancel npc spawn events.
     *
     * @param npc    the Npc to check
     * @param player the player to check conditions with
     * @return if the npc is supposed to be hidden
     * @see #isHidden(NpcIdentifier, OnlineProfile)
     * @see Npc#hide(OnlineProfile)
     * @see Npc#show(OnlineProfile)
     */
    boolean isHidden(Npc<?> npc, Player player);
}
