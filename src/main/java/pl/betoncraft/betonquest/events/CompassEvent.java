package pl.betoncraft.betonquest.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestCompassTargetChangeEvent;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.location.CompoundLocation;

import java.util.logging.Level;

/**
 * Adds a compass specific tag to the player.
 */
public class CompassEvent extends QuestEvent {

    private final Action action;
    private final String compass;
    private ConfigurationSection compassSection;
    private ConfigPackage compassPackage;

    public CompassEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        persistent = true;

        action = instruction.getEnum(Action.class);
        compass = instruction.next();

        // Check if compass is valid
        for (final ConfigPackage pack : Config.getPackages().values()) {
            final ConfigurationSection section = pack.getMain().getConfig().getConfigurationSection("compass");
            if (section != null) {
                if (section.contains(compass)) {
                    compassSection = section.getConfigurationSection(compass);
                    compassPackage = pack;
                    break;
                }
            }
        }
        if (compassSection == null) {
            throw new InstructionParseException("Invalid compass location: " + compass);
        }
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        switch (action) {
            case ADD:
            case DEL:
                // Add Tag to player
                try {
                    new TagEvent(new Instruction(instruction.getPackage(), null, "tag " + action.toString().toLowerCase() + " compass-" + compass)).handle(playerID);
                } catch (InstructionParseException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Failed to tag player with compass point: " + compass);
                    LogUtils.logThrowable(e);
                }
                return null;
            case SET:
                final Location location;
                try {
                    location = new CompoundLocation(compassPackage.getName(), compassSection.getString("location")).getLocation(playerID);
                } catch (QuestRuntimeException | InstructionParseException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Failed to set compass: " + compass);
                    LogUtils.logThrowable(e);
                    return null;
                }

                final Player player = PlayerConverter.getPlayer(playerID);
                if (player != null) {
                    final QuestCompassTargetChangeEvent event = new QuestCompassTargetChangeEvent(location);
                    Bukkit.getServer().getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        player.setCompassTarget(location);
                    }
                }
        }
        return null;
    }

    public enum Action {
        ADD,
        DEL,
        SET
    }
}
