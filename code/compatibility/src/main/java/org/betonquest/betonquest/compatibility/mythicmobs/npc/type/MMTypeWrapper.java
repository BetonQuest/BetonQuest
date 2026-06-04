package org.betonquest.betonquest.compatibility.mythicmobs.npc.type;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicHider;
import org.betonquest.betonquest.compatibility.mythicmobs.npc.MythicMobsNpcAdapter;
import org.betonquest.betonquest.compatibility.mythicmobs.npc.WrappingMMNpcAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Gets the Mob Npc by their {@link MythicMob} definition.
 */
public class MMTypeWrapper extends GenericMythicTypeWrapper {

    /**
     * One amount.
     */
    private static final int ONE = 1;

    /**
     * Identifying mythic mob type.
     */
    private final Argument<MythicMob> type;

    /**
     * Creates a new instance.
     *
     * @param mobExecutor the instance to get the mob from
     * @param mythicHider the hider for mobs
     * @param type        the identifying mythic mob type
     */
    public MMTypeWrapper(final MobExecutor mobExecutor, final MythicHider mythicHider, final Argument<MythicMob> type) {
        super(mobExecutor, mythicHider, "type");
        this.type = type;
    }

    @Override
    public Npc<ActiveMob> getNpc(@Nullable final Profile profile) throws QuestException {
        final MythicMob type = this.type.getValue(profile);
        final Collection<ActiveMob> activeMobs = mobExecutor.getActiveMobs(mob -> type.equals(mob.getType()));
        if (activeMobs.isEmpty()) {
            return new WrappingMMNpcAdapter(type, mythicHider);
        }
        if (activeMobs.size() != ONE) {
            throw new QuestException("There exists multiple MythicMobs with type '%s', can't determine!".formatted(type));
        }
        return new MythicMobsNpcAdapter(activeMobs.iterator().next(), mythicHider);
    }

    @Override
    public Set<Npc<ActiveMob>> getNpcs(@Nullable final Profile profile) throws QuestException {
        final MythicMob type = this.type.getValue(profile);
        final Collection<ActiveMob> activeMobs = mobExecutor.getActiveMobs(mob -> type.equals(mob.getType()));
        if (activeMobs.isEmpty()) {
            return Set.of(new WrappingMMNpcAdapter(type, mythicHider));
        }
        return activeMobs.stream()
                .map(activeMob -> new MythicMobsNpcAdapter(activeMob, mythicHider))
                .collect(Collectors.toSet());
    }
}
