package pl.betoncraft.betonquest.compatibility.effectlib;

import de.slikey.effectlib.util.DynamicLocation;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Displays an effect.
 */
public class ParticleEvent extends QuestEvent {

    private final String effectClass;
    private final ConfigurationSection parameters;
    private final LocationData loc;
    private final boolean pr1vate;

    public ParticleEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String string = instruction.next();
        parameters = instruction.getPackage().getCustom().getConfig().getConfigurationSection("effects." + string);
        if (parameters == null) {
            throw new InstructionParseException("Effect '" + string + "' does not exist!");
        }
        effectClass = parameters.getString("class");
        if (effectClass == null) {
            throw new InstructionParseException("Effect '" + string + "' is incorrectly defined");
        }
        loc = instruction.getLocation(instruction.getOptional("loc"));
        pr1vate = instruction.hasArgument("private");

    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        final Location location = (loc == null) ? player.getLocation() : loc.getLocation(playerID);
        // This is not used at the moment
        // Entity originEntity = (loc == null) ? p : null;
        final Player targetPlayer = pr1vate ? player : null;
        EffectLibIntegrator.getEffectManager().start(effectClass,
                parameters,
                new DynamicLocation(location, null),
                new DynamicLocation(null, null),
                (ConfigurationSection) null,
                targetPlayer);
        return null;
    }

}
