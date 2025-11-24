package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * Citizens wrapper to get a Npc.
 */
class CitizensWrapper implements NpcWrapper<NPC> {
    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Source Registry of NPCs to use.
     */
    private final NPCRegistry registry;

    /**
     * Id of the Npc.
     */
    private final Variable<Number> npcId;

    /**
     * Create a new Citizens Npc Wrapper.
     *
     * @param plugin   the plugin instance
     * @param registry the registry of NPCs to use
     * @param npcId    the id of the Npc, greater or equals to zero
     */
    public CitizensWrapper(final Plugin plugin, final NPCRegistry registry, final Variable<Number> npcId) {
        this.plugin = plugin;
        this.registry = registry;
        this.npcId = npcId;
    }

    @Override
    public Npc<NPC> getNpc(@Nullable final Profile profile) throws QuestException {
        final int npcId = this.npcId.getValue(profile).intValue();
        final NPC npc = registry.getById(npcId);
        if (npc == null) {
            throw new QuestException("NPC with ID " + npcId + " not found");
        }
        return new CitizensAdapter(plugin, npc);
    }
}
