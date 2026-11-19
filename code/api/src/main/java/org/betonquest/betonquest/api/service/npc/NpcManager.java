package org.betonquest.betonquest.api.service.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * The NpcManager is responsible for handling NPCs known to BetonQuest.
 * <p>
 * Each NPC is identified by an {@link NpcIdentifier}, which consists of the user-defined name
 * in the configuration and the {@link QuestPackage} it belongs to.
 *
 * @since 3.0.0
 */
public interface NpcManager {

    /**
     * Retrieves a single {@link Npc} by its {@link NpcIdentifier}.
     * <p>
     * If multiple NPCs match the identifier, a {@link QuestException} is thrown.
     * The specified {@link Profile} is used to resolve placeholders in the NPC's instruction.
     * If no profile is provided, placeholders requiring one will cause the resolution to fail.
     *
     * @param profile       the profile to resolve the NPC for, or {@code null} if no profile is involved
     * @param npcIdentifier the identifier of the NPC to retrieve
     * @return the NPC for the given identifier
     * @throws QuestException if no NPC or multiple NPCs exist with the given identifier, or if resolution fails
     * @see #getAll(Profile, NpcIdentifier) for retrieving multiple NPCs matching an identifier
     * @since 3.0.0
     */
    @Contract(pure = true)
    Npc<?> get(@Nullable Profile profile, NpcIdentifier npcIdentifier) throws QuestException;

    /**
     * Retrieves all {@link Npc}s matching the given {@link NpcIdentifier}.
     * <p>
     * The specified {@link Profile} is used to resolve placeholders in the NPCs' instructions.
     * If no profile is provided, placeholders requiring one will cause the resolution to fail.
     *
     * @param profile       the profile to resolve the NPCs for, or {@code null} if no profile is involved
     * @param npcIdentifier the identifier of the NPCs to retrieve
     * @return a set of NPCs matching the given identifier
     * @throws QuestException if no NPCs exist with the given identifier or if resolution fails
     * @see #get(Profile, NpcIdentifier) for retrieving a single NPC matching an identifier
     * @since 3.0.0
     */
    @Contract(pure = true)
    Set<Npc<?>> getAll(@Nullable Profile profile, NpcIdentifier npcIdentifier) throws QuestException;

    /**
     * Checks if the NPC with the specified {@link NpcIdentifier} should be hidden for the given {@link OnlineProfile}.
     * <p>
     * This check evaluates the conditions defined in the {@code hide_npcs} section.
     * It can be used, for example, to cancel NPC spawn events.
     *
     * @param npcId   the identifier of the NPC
     * @param profile the profile to check conditions for
     * @return {@code true} if the NPC should be hidden, {@code false} otherwise
     * @see #isHidden(Npc, Player)
     * @see Npc#hide(OnlineProfile)
     * @see Npc#show(OnlineProfile)
     * @since 3.0.0
     */
    @Contract(pure = true)
    boolean isHidden(NpcIdentifier npcId, OnlineProfile profile);

    /**
     * Checks if the specified {@link Npc} should be hidden for the given {@link Player}.
     * <p>
     * This check evaluates the conditions defined in the {@code hide_npcs} section.
     * It can be used, for example, to cancel NPC spawn events.
     *
     * @param npc    the NPC to check
     * @param player the player to check conditions for
     * @return {@code true} if the NPC should be hidden, {@code false} otherwise
     * @see #isHidden(NpcIdentifier, OnlineProfile)
     * @see Npc#hide(OnlineProfile)
     * @see Npc#show(OnlineProfile)
     * @since 3.0.0
     */
    @Contract(pure = true)
    boolean isHidden(Npc<?> npc, Player player);
}
