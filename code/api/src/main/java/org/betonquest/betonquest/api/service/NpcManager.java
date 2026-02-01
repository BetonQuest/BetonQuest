package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.jetbrains.annotations.Nullable;

/**
 * The NpcManager is responsible for handling NPCs known to BetonQuest.
 * <br> <br>
 * Each npc is uniquely identified by a {@link NpcIdentifier} which consists of the user-defined name in the configuration
 * as well as the {@link QuestPackage} the npc belongs to.
 */
@FunctionalInterface
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
}
