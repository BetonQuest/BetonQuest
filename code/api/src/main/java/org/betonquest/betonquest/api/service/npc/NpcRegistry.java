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
 * A registry for NPC factories and identifiers.
 *
 * @since 3.0.0
 */
public interface NpcRegistry extends FeatureTypeRegistry<NpcWrapper<?>> {

    /**
     * Registers a reverse-identifier to enable matching NPCs to their IDs used within BetonQuest.
     *
     * @param identifier the reverse-identifier to register
     * @since 3.0.0
     */
    @Contract(mutates = "this")
    void registerIdentifier(NpcReverseIdentifier identifier);

    /**
     * Returns the identifiers used to identify the given NPC.
     * <br><br>
     * The specified profile may influence the resolution process.
     *
     * @param npc     the NPC to get identifiers for
     * @param profile the profile potentially influencing the identifier resolution, or {@code null}
     * @return a set of identifiers used in BetonQuest to identify the NPC
     * @since 3.0.0
     */
    @Contract(pure = true)
    Set<NpcIdentifier> getIdentifier(Npc<?> npc, @Nullable OnlineProfile profile);
}
