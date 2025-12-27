package org.betonquest.betonquest.compatibility.npc.fancynpcs;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcManager;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.jetbrains.annotations.Nullable;

/**
 * FancyNpcs wrapper to get a Npc.
 */
public class FancyWrapper implements NpcWrapper<Npc> {

    /**
     * FancyNpcs Npc Manager.
     */
    private final NpcManager npcManager;

    /**
     * Npc identifier.
     */
    private final Argument<String> npcId;

    /**
     * If the identifier should be interpreted as name.
     */
    private final FlagArgument<Boolean> byName;

    /**
     * Create a new FancyNpcs Npc Wrapper.
     *
     * @param npcManager the Npc Manager to get Npcs from
     * @param npcId      the npc identifier
     * @param byName     whether to use the identifier as name or id
     */
    public FancyWrapper(final NpcManager npcManager, final Argument<String> npcId, final FlagArgument<Boolean> byName) {
        this.npcManager = npcManager;
        this.npcId = npcId;
        this.byName = byName;
    }

    @Override
    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    public org.betonquest.betonquest.api.quest.npc.Npc<Npc> getNpc(@Nullable final Profile profile) throws QuestException {
        Npc npc = null;
        final String npcId = this.npcId.getValue(profile);
        if (byName.getValue(profile).orElse(false)) {
            for (final Npc aNpc : npcManager.getAllNpcs()) {
                if (npcId.equals(aNpc.getData().getName())) {
                    if (npc != null) {
                        throw new QuestException("Multiple Npcs with the same name: " + npcId);
                    }
                    npc = aNpc;
                }
            }
        } else {
            npc = npcManager.getNpcById(npcId);
        }
        if (npc == null) {
            throw new QuestException("Fancy Npc with " + (byName.getValue(profile).orElse(false) ? "name" : "id") + " " + npcId + " not found");
        }
        return new FancyAdapter(npc);
    }
}
