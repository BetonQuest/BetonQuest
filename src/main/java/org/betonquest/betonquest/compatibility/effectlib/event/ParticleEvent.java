package org.betonquest.betonquest.compatibility.effectlib.event;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.util.DynamicLocation;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Displays an effect.
 */
public class ParticleEvent implements Event {
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
    private final VariableLocation loc;

    /**
     * If the particle should be only visible for the player.
     */
    private final boolean privateParticle;

    /**
     * Create a new EffectLib Particle event.
     *
     * @param manager         the effect manager which will create and control the particles
     * @param parameters      the effects' parameter defining its appearance
     * @param effectClass     the name of the effect class
     * @param loc             the particles' root location or null if players' location should be used
     * @param privateParticle if the particle should be only visible for the player
     */
    public ParticleEvent(final EffectManager manager, final String effectClass, final ConfigurationSection parameters,
                         @Nullable final VariableLocation loc, final boolean privateParticle) {
        this.manager = manager;
        this.effectClass = effectClass;
        this.parameters = parameters;
        this.loc = loc;
        this.privateParticle = privateParticle;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        final Location location = (loc == null) ? player.getLocation() : loc.getValue(profile);
        // This is not used at the moment
        // Entity originEntity = (loc == null) ? p : null;
        final Player targetPlayer = privateParticle ? player : null;
        manager.start(effectClass,
                parameters,
                new DynamicLocation(location, null),
                new DynamicLocation(null, null),
                (ConfigurationSection) null,
                targetPlayer);
    }
}
