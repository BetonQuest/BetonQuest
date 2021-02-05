package org.betonquest.betonquest.events;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestCompassTargetChangeEvent;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Locale;

/**
 * Adds a compass specific tag to the player.
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
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
            if (section != null && section.contains(compass)) {
                compassSection = section.getConfigurationSection(compass);
                compassPackage = pack;
                break;
            }
        }
        if (compassSection == null) {
            throw new InstructionParseException("Invalid compass location: " + compass);
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected Void execute(final String playerID) throws QuestRuntimeException {
        switch (action) {
            case ADD:
            case DEL:
                // Add Tag to player
                try {
                    new TagEvent(new Instruction(instruction.getPackage(), null, "tag " + action.toString().toLowerCase(Locale.ROOT) + " compass-" + compass)).handle(playerID);
                } catch (final InstructionParseException e) {
                    LOG.warning(instruction.getPackage(), "Failed to tag player with compass point: " + compass, e);
                }
                return null;
            case SET:
                final Location location;
                try {
                    location = new CompoundLocation(compassPackage.getName(), compassSection.getString("location")).getLocation(playerID);
                } catch (QuestRuntimeException | InstructionParseException e) {
                    LOG.warning(instruction.getPackage(), "Failed to set compass: " + compass, e);
                    return null;
                }

                final Player player = PlayerConverter.getPlayer(playerID);
                if (player != null) {
                    final QuestCompassTargetChangeEvent event = new QuestCompassTargetChangeEvent(location);
                    Bukkit.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
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
