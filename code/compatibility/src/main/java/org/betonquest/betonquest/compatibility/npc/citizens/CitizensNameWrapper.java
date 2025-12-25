package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * Citizens wrapper to get a Npc.
 */
public class CitizensNameWrapper implements NpcWrapper<NPC> {

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Source Registry of NPCs to use.
     */
    private final NPCRegistry registry;

    /**
     * Name of the Npc.
     */
    private final Argument<String> npcName;

    /**
     * Create a new Citizens Npc Wrapper.
     *
     * @param plugin   the plugin instance
     * @param registry the registry of NPCs to use
     * @param npcName  the name of the Npc
     */
    public CitizensNameWrapper(final Plugin plugin, final NPCRegistry registry, final Argument<String> npcName) {
        this.plugin = plugin;
        this.registry = registry;
        this.npcName = npcName;
    }

    @Override
    public Npc<NPC> getNpc(@Nullable final Profile profile) throws QuestException {
        NPC selectedNpc = null;
        final String npcName = this.npcName.getValue(profile);
        for (final NPC npc : registry) {
            if (npc.getName().equals(npcName)) {
                if (selectedNpc == null) {
                    selectedNpc = npc;
                } else {
                    throw new QuestException("Multiple NPCs with the same name: " + npcName);
                }
            }
        }
        if (selectedNpc == null) {
            throw new QuestException("NPC with name '" + npcName + "' not found");
        }
        return new CitizensAdapter(plugin, selectedNpc);
    }
}
