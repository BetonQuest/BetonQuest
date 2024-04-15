package org.betonquest.betonquest.compatibility.effectlib;

import de.slikey.effectlib.util.DynamicLocation;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Displays an effect.
 */
@SuppressWarnings("PMD.CommentRequired")
public class ParticleEvent extends QuestEvent {
    private final String effectClass;

    @Nullable
    private final ConfigurationSection parameters;

    @Nullable
    private final CompoundLocation loc;

    private final boolean privateParticle;

    public ParticleEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String string = instruction.next();
        parameters = instruction.getPackage().getConfig().getConfigurationSection("effects." + string);
        if (parameters == null) {
            throw new InstructionParseException("Effect '" + string + "' does not exist!");
        }
        //noinspection DataFlowIssue
        effectClass = parameters.getString("class");
        if (effectClass == null) {
            throw new InstructionParseException("Effect '" + string + "' is incorrectly defined");
        }
        loc = instruction.getLocation(instruction.getOptional("loc"));
        privateParticle = instruction.hasArgument("private");

    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        final Location location = (loc == null) ? player.getLocation() : loc.getLocation(profile);
        // This is not used at the moment
        // Entity originEntity = (loc == null) ? p : null;
        final Player targetPlayer = privateParticle ? player : null;
        EffectLibIntegrator.getEffectManager().start(effectClass,
                parameters,
                new DynamicLocation(location, null),
                new DynamicLocation(null, null),
                (ConfigurationSection) null,
                targetPlayer);
        return null;
    }

}
