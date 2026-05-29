package org.betonquest.betonquest.compatibility.mythicmobs.npc.type;

import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicHider;
import org.betonquest.betonquest.compatibility.mythicmobs.npc.MythicMobsNpcAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Gets the Mob Npc by {@link UUID}.
 */
public class MMUUIDWrapper extends GenericMythicTypeWrapper {

    /**
     * Identifying uuid of entity.
     */
    private final Argument<UUID> uuid;

    /**
     * Creates a new instance.
     *
     * @param mobExecutor the instance to get the mob from
     * @param mythicHider the hider for mobs
     * @param uuid        the identifying uuid of the entity
     */
    public MMUUIDWrapper(final MobExecutor mobExecutor, final MythicHider mythicHider, final Argument<UUID> uuid) {
        super(mobExecutor, mythicHider, "entity UUID");
        this.uuid = uuid;
    }

    @Override
    public Npc<ActiveMob> getNpc(@Nullable final Profile profile) throws QuestException {
        final UUID uuid = this.uuid.getValue(profile);
        final Optional<ActiveMob> activeMob = mobExecutor.getActiveMob(uuid);
        if (activeMob.isPresent()) {
            return new MythicMobsNpcAdapter(activeMob.get(), mythicHider);
        }
        throw new QuestException("Could not find entity '%s' for MythicMob Npc".formatted(uuid));
    }

    @Override
    public Set<Npc<ActiveMob>> getNpcs(@Nullable final Profile profile) throws QuestException {
        return Set.of(getNpc(profile));
    }
}
