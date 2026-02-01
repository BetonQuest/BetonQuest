package org.betonquest.betonquest.api.quest.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.identifier.factory.IdentifierRegistry;
import org.betonquest.betonquest.api.instruction.InstructionApi;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.kernel.processor.quest.NpcProcessor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Hides (or shows) Npcs based on conditions defined in the {@code hide_npcs} section of a {@link QuestPackage}.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class NpcHider {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    protected final BetonQuestLogger log;

    /**
     * Processor to get Npcs.
     */
    private final NpcProcessor npcProcessor;

    /**
     * The Quest Type API to check hiding conditions.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Npc types to get NpcIds from a Npc.
     */
    private final NpcRegistry npcTypes;

    /**
     * Identifier registry to get identifiers from.
     */
    private final IdentifierRegistry identifierRegistry;

    /**
     * Npc ids mapped to their hide conditions.
     */
    private final Map<NpcIdentifier, Set<ConditionIdentifier>> npcs;

    /**
     * Instruction API to resolve {@link SectionInstruction}s.
     */
    private final InstructionApi instructionApi;

    /**
     * The task refreshing npc visibility.
     */
    @Nullable
    private BukkitTask task;

    /**
     * Create and start a new Npc Hider.
     *
     * @param log                the custom logger for this class
     * @param npcProcessor       the processor to get nps
     * @param questTypeApi       the Quest Type API to check hiding conditions
     * @param profileProvider    the profile provider instance
     * @param npcTypes           the Npc types to get NpcIds
     * @param identifierRegistry the identifier registry to get identifiers from
     * @param instructionApi     the instruction api to resolve sections
     */
    public NpcHider(final BetonQuestLogger log, final NpcProcessor npcProcessor,
                    final QuestTypeApi questTypeApi, final ProfileProvider profileProvider,
                    final NpcRegistry npcTypes, final IdentifierRegistry identifierRegistry,
                    final InstructionApi instructionApi) {
        this.log = log;
        this.identifierRegistry = identifierRegistry;
        this.instructionApi = instructionApi;
        this.npcProcessor = npcProcessor;
        this.questTypeApi = questTypeApi;
        this.profileProvider = profileProvider;
        this.npcTypes = npcTypes;
        this.npcs = new HashMap<>();
    }

    /**
     * Load all npc hidings from the QuestPackage.
     *
     * @param pack to load from
     */
    public void load(final QuestPackage pack) {
        final ConfigurationSection section = pack.getConfig().getConfigurationSection("hide_npcs");
        if (section == null) {
            return;
        }
        for (final String idString : section.getKeys(false)) {
            try {
                final SectionInstruction sectionInstruction = instructionApi.createSectionInstruction(pack, section);
                loadKey(sectionInstruction, idString);
            } catch (final QuestException e) {
                log.warn("Could not load hide_npcs '" + idString + "' in pack '" + pack.getQuestPath() + "': " + e.getMessage(), e);
            }
        }
    }

    private void loadKey(final SectionInstruction instruction, final String idString) throws QuestException {
        final IdentifierFactory<NpcIdentifier> npcIdentifierFactory = identifierRegistry.getFactory(NpcIdentifier.class);
        final NpcIdentifier npcId = npcIdentifierFactory.parseIdentifier(instruction.getPackage(), idString);
        final List<ConditionIdentifier> conditions = instruction.read().value(idString)
                .identifier(ConditionIdentifier.class).list().get().getValue(null);
        if (npcs.containsKey(npcId)) {
            npcs.get(npcId).addAll(conditions);
        } else {
            npcs.put(npcId, new HashSet<>(conditions));
        }
    }

    /**
     * Reloads the Npc Hider, restarting runnable etc.
     * Individual packs need to be loaded with the {@link #load(QuestPackage)}
     *
     * @param updateInterval the interval in ticks to check refresh hiding
     * @param plugin         the plugin instance to schedule update
     */
    public void reload(final int updateInterval, final Plugin plugin) {
        if (task != null) {
            task.cancel();
        }
        npcs.clear();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                applyVisibility();
            }
        }.runTaskTimer(plugin, 0, updateInterval);
    }

    /**
     * Checks if the Npc should be invisible to the player.
     * <p>
     * This is primary used to cancel Npc spawn events.
     *
     * @param npcId   the id of the Npc
     * @param profile the profile to check conditions for
     * @return if the npc is stored and the hide conditions are met
     */
    public boolean isHidden(final NpcIdentifier npcId, final OnlineProfile profile) {
        final Set<ConditionIdentifier> conditions = npcs.get(npcId);
        if (conditions == null || conditions.isEmpty()) {
            return false;
        }
        return questTypeApi.conditions(profile, conditions);
    }

    /**
     * Allows to check if a Npc should be hidden.
     *
     * @param npc    the Npc to check
     * @param player the player to check conditions with
     * @return if the Npc is hidden with a Npc Hider
     */
    public boolean isHidden(final Npc<?> npc, final Player player) {
        final OnlineProfile onlineProfile = profileProvider.getProfile(player);
        final Set<NpcIdentifier> identifier = npcTypes.getIdentifier(npc, onlineProfile);
        if (identifier.isEmpty()) {
            return false;
        }
        for (final NpcIdentifier npcID : identifier) {
            if (isHidden(npcID, onlineProfile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the visibility of the specified Npc for this player.
     *
     * @param onlineProfile the online profile of the player
     * @param npcId         the id of the Npc
     */
    public void applyVisibility(final OnlineProfile onlineProfile, final NpcIdentifier npcId) {
        final Set<ConditionIdentifier> conditions = npcs.get(npcId);
        if (conditions == null) {
            return;
        }
        final Npc<?> npc;
        try {
            npc = npcProcessor.get(npcId).getNpc(onlineProfile);
        } catch (final QuestException exception) {
            log.warn("NPCHider could not update visibility for npc '" + npcId + "': " + exception.getMessage(), exception);
            return;
        }
        if (npc.isSpawned()) {
            if (conditions.isEmpty() || questTypeApi.conditions(onlineProfile, conditions)) {
                npc.hide(onlineProfile);
            } else {
                npc.show(onlineProfile);
            }
        }
    }

    /**
     * Updates the visibility of all Npcs for this player.
     *
     * @param onlineProfile the online profile of the player
     */
    public void applyVisibility(final OnlineProfile onlineProfile) {
        for (final NpcIdentifier npcId : npcs.keySet()) {
            applyVisibility(onlineProfile, npcId);
        }
    }

    /**
     * Updates the visibility of this Npc for all players.
     *
     * @param npcId the id of the Npc
     */
    public void applyVisibility(final NpcIdentifier npcId) {
        for (final OnlineProfile onlineProfile : profileProvider.getOnlineProfiles()) {
            applyVisibility(onlineProfile, npcId);
        }
    }

    /**
     * Updates the visibility of all Npcs for all OnlineProfiles.
     */
    public void applyVisibility() {
        for (final OnlineProfile onlineProfile : profileProvider.getOnlineProfiles()) {
            for (final NpcIdentifier npcId : npcs.keySet()) {
                applyVisibility(onlineProfile, npcId);
            }
        }
    }
}
