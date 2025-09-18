package org.betonquest.betonquest.api.quest.npc;

import org.betonquest.betonquest.api.kernel.FeatureTypeRegistry;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Stores the npc factories and identifier.
 */
public interface NpcRegistry extends FeatureTypeRegistry<NpcWrapper<?>> {
    /**
     * Registers a reverse-identifier to allow matching npcs to their in BQ used IDs.
     *
     * @param identifier the object to register reverse used npc ids
     */
    void registerIdentifier(NpcReverseIdentifier identifier);

    /**
     * Gets the IDs used to get a Npc.
     *
     * @param npc     the npc to get the npc ids
     * @param profile the related profile potentially influencing resolving
     * @return the ids used in BetonQuest to identify the Npc
     */
    Set<NpcID> getIdentifier(Npc<?> npc, @Nullable OnlineProfile profile);
}
