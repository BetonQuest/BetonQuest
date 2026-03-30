package org.betonquest.betonquest.compatibility.npc.fancynpcs;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcManager;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.bukkit.plugin.Plugin;

/**
 * Factory to get FancyNpcs Npcs.
 */
public class FancyFactory implements NpcFactory {

    /**
     * The plugin instance to run tasks on.
     */
    private final Plugin plugin;

    /**
     * The empty default constructor.
     *
     * @param plugin the plugin instance to run tasks on
     */
    public FancyFactory(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public NpcWrapper<Npc> parseInstruction(final Instruction instruction) throws QuestException {
        final NpcManager npcManager = FancyNpcsPlugin.get().getNpcManager();
        return new FancyWrapper(plugin, npcManager, instruction.string().get(), instruction.bool().getFlag("byName", true));
    }
}
