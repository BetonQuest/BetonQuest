package org.betonquest.betonquest.quest.registry.type;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.api.quest.npc.NpcWrapper;
import org.betonquest.betonquest.api.quest.npc.feature.NpcInteractCatcher;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.quest.registry.FactoryRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Stores the npc types that can be used in BetonQuest.
 */
public class NpcTypeRegistry extends FactoryRegistry<TypeFactory<NpcWrapper<?>>> {
    /**
     * Npc Classes mapped to their Factory to get the instruction string.
     */
    private final Map<Class<?>, Map.Entry<String, NpcFactory<?>>> mapping;

    /**
     * Maps the contents of ids to the ids having that content.
     */
    private final Map<String, Set<NpcID>> idsByInstruction;

    /**
     * Create a new npc type registry.
     *
     * @param log the logger that will be used for logging
     */
    public NpcTypeRegistry(final BetonQuestLogger log) {
        super(log, "npc");
        this.mapping = new HashMap<>();
        this.idsByInstruction = new HashMap<>();
    }

    /**
     * Registers a npc factory with a {@link NpcInteractCatcher} to convert the third party interactions.
     *
     * @param name    the name of the type
     * @param factory the player factory to create the type
     * @param <T>     the original npc type
     */
    public <T> void register(final String name, final NpcFactory<T> factory) {
        register(name, factory::parseInstruction); // TODO irgendwas mit generics…
        mapping.put(factory.factoredClass(), Map.entry(name, factory));
    }

    /**
     * Adds the id to the "instruction -> ID" mapping to identify external npc interaction.
     *
     * @param npcId the id to add store in the mapping
     */
    public void addIdentifier(final NpcID npcId) {
        idsByInstruction.computeIfAbsent(npcId.getInstruction().toString(), string -> new HashSet<>()).add(npcId);
    }

    /**
     * Gets the IDs used to get a Npc.
     *
     * @param npc the npc to get the npc ids
     * @param <T> the original type of the npc
     * @return the ids used in BetonQuest to identify the Npc
     * @throws IllegalArgumentException if no factory for that Npc type is registered
     */
    public <T> Set<NpcID> getIdentifier(final Npc<T> npc) {
        final Map.Entry<String, NpcFactory<?>> entry = mapping.get(npc.getClass());
        if (entry == null) {
            throw new IllegalArgumentException("Npc " + npc.getClass().getName() + " does not have a factory");
        }
        @SuppressWarnings("unchecked") final NpcFactory<T> factory = (NpcFactory<T>) entry.getValue();
        final String prefix = entry.getKey() + " ";
        final Set<NpcID> npcIds = new HashSet<>();
        for (final String instruction : factory.npcInstructionStrings(npc)) {
            npcIds.addAll(idsByInstruction.get(prefix + instruction));
        }
        return npcIds;
    }
}
