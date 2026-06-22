package org.betonquest.betonquest.api.service.npc;

import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.FeatureTypeRegistry;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcReverseIdentifier;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Stores the npc factories and identifier.
 *
 * @since 3.0.0
 */
public interface NpcRegistry extends FeatureTypeRegistry<NpcWrapper<?>> {

    /**
     * Registers a reverse-identifier to allow matching npcs to their in BQ used IDs.
     *
     * @param identifier the object to register reversely used npc ids
     * @since 3.0.0
     */
    @Contract(mutates = "this")
    void registerIdentifier(NpcReverseIdentifier identifier);

    /**
     * Gets the IDs used to get a Npc.
     *
     * @param npc     the npc to get the npc ids
     * @param profile the related profile potentially influencing resolving
     * @return the ids used in BetonQuest to identify the Npc
     * @since 3.0.0
     */
    @Contract(pure = true)
    Set<NpcIdentifier> getIdentifier(Npc<?> npc, @Nullable OnlineProfile profile);
}
