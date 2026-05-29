package org.betonquest.betonquest.compatibility.mythicmobs.npc.type;

import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.compatibility.mythicmobs.MythicHider;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Gets the Mob Npc by their {@link ActiveMob#getFaction()}.
 */
public class MMFactionWrapper extends GenericMythicTypeWrapper {

    /**
     * Identifying faction.
     */
    private final Argument<String> faction;

    /**
     * Creates a new instance.
     *
     * @param mobExecutor the instance to get the mob from
     * @param mythicHider the hider for mobs
     * @param faction     the identifying faction
     */
    public MMFactionWrapper(final MobExecutor mobExecutor, final MythicHider mythicHider, final Argument<String> faction) {
        super(mobExecutor, mythicHider, "faction");
        this.faction = faction;
    }

    @Override
    public Npc<ActiveMob> getNpc(@Nullable final Profile profile) throws QuestException {
        final String faction = this.faction.getValue(profile);
        return getOne(mob -> faction.equals(mob.getFaction()), faction);
    }

    @Override
    public Set<Npc<ActiveMob>> getNpcs(@Nullable final Profile profile) throws QuestException {
        final String faction = this.faction.getValue(profile);
        return getAll(mob -> faction.equals(mob.getFaction()), faction);
    }
}
