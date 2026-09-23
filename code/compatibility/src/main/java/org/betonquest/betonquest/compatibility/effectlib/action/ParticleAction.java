package org.betonquest.betonquest.compatibility.effectlib.action;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.util.DynamicLocation;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Displays an effect.
 */
public class ParticleAction implements OnlineAction {

    /**
     * Effect manager which will create and control the particles.
     */
    private final EffectManager manager;

    /**
     * Name of the effect class.
     */
    private final String effectClass;

    /**
     * Effects' parameter defining its appearance.
     */
    private final ConfigurationSection parameters;

    /**
     * The particles' root location.
     */
    @Nullable
    private final Argument<Location> loc;

    /**
     * The particles' target location.
     */
    @Nullable
    private final Argument<Location> targetLoc;

    /**
     * If the particle should be only visible for the player.
     */
    private final FlagArgument<Boolean> privateParticle;

    /**
     * Create a new EffectLib Particle action.
     *
     * @param manager         the effect manager which will create and control the particles
     * @param parameters      the effects' parameter defining its appearance
     * @param effectClass     the name of the effect class
     * @param loc             the particles' root location or null if players' location should be used
     * @param targetLoc       the particles' target location or null if none should be used
     * @param privateParticle if the particle should be only visible for the player
     */
    public ParticleAction(final EffectManager manager, final String effectClass, final ConfigurationSection parameters,
                          @Nullable final Argument<Location> loc, @Nullable final Argument<Location> targetLoc, final FlagArgument<Boolean> privateParticle) {
        this.manager = manager;
        this.effectClass = effectClass;
        this.parameters = parameters;
        this.loc = loc;
        this.targetLoc = targetLoc;
        this.privateParticle = privateParticle;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final Location location = (loc == null) ? player.getLocation() : loc.getValue(profile);
        final Location targetLocation = targetLoc == null ? null : targetLoc.getValue(profile);
        final Player targetPlayer = privateParticle.getValue(profile).orElse(false) ? player : null;
        final Object effect = manager.start(effectClass,
                parameters,
                new DynamicLocation(location, null),
                new DynamicLocation(targetLocation, null),
                (ConfigurationSection) null,
                targetPlayer);
        if (effect == null) {
            throw new QuestException("Could not start effect '%s'".formatted(effectClass));
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
