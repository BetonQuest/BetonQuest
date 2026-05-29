package org.betonquest.betonquest.compatibility.npc.fancynpcs;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcManager;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * FancyNpcs wrapper to get a Npc.
 */
public class FancyWrapper implements NpcWrapper<Npc> {

    /**
     * The plugin instance to run tasks on.
     */
    private final Plugin plugin;

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
     * @param plugin     the plugin instance to run tasks on
     * @param npcManager the Npc Manager to get Npcs from
     * @param npcId      the npc identifier
     * @param byName     whether to use the identifier as name or id
     */
    public FancyWrapper(final Plugin plugin, final NpcManager npcManager, final Argument<String> npcId, final FlagArgument<Boolean> byName) {
        this.plugin = plugin;
        this.npcManager = npcManager;
        this.npcId = npcId;
        this.byName = byName;
    }

    @Override
    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    public org.betonquest.betonquest.api.quest.npc.Npc<Npc> getNpc(@Nullable final Profile profile) throws QuestException {
        Npc npc = null;
        final String npcId = this.npcId.getValue(profile);
        final boolean byName = this.byName.getValue(profile).orElse(false);
        if (byName) {
            for (final Npc aNpc : npcManager.getAllNpcs()) {
                if (npcId.equals(aNpc.getData().getName())) {
                    if (npc != null) {
                        throw new QuestException("Multiple Fancy Npcs with the same name: " + npcId);
                    }
                    npc = aNpc;
                }
            }
        } else {
            npc = npcManager.getNpcById(npcId);
        }
        if (npc == null) {
            throw new QuestException("Fancy Npc with %s %s not found".formatted(byName ? "name" : "id", npcId));
        }
        return new FancyAdapter(plugin, npc);
    }

    @Override
    public Set<org.betonquest.betonquest.api.quest.npc.Npc<Npc>> getNpcs(@Nullable final Profile profile) throws QuestException {
        final Set<Npc> npcs = new HashSet<>();
        final String npcId = this.npcId.getValue(profile);
        final boolean byName = this.byName.getValue(profile).orElse(false);
        if (byName) {
            for (final Npc aNpc : npcManager.getAllNpcs()) {
                if (npcId.equals(aNpc.getData().getName())) {
                    npcs.add(aNpc);
                }
            }
        } else {
            final Npc aNpc = npcManager.getNpcById(npcId);
            if (aNpc != null) {
                npcs.add(aNpc);
            }
        }
        if (npcs.isEmpty()) {
            throw new QuestException("No Fancy Npc with %s %s found".formatted(byName ? "name" : "id", npcId));
        }
        return npcs.stream().map(npc -> new FancyAdapter(plugin, npc)).collect(Collectors.toSet());
    }
}
