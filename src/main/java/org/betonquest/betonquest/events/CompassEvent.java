package org.betonquest.betonquest.events;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestCompassTargetChangeEvent;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Locale;

/**
 * Adds a compass specific tag to the player.
 */
@SuppressWarnings("PMD.CommentRequired")
public class CompassEvent extends QuestEvent {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    private final Action action;

    private final String compass;

    private CompoundLocation compassLocation;

    public CompassEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
        persistent = true;

        action = instruction.getEnum(Action.class);
        compass = instruction.next();
        compassLocation = getCompassLocation();
    }

    private CompoundLocation getCompassLocation() throws InstructionParseException {
        // Check if compass is valid
        for (final QuestPackage pack : Config.getPackages().values()) {
            final ConfigurationSection section = pack.getConfig().getConfigurationSection("compass");
            if (section != null && section.contains(compass)) {
                return new CompoundLocation(pack, pack.getString("compass." + compass + ".location"));
            }
        }
        throw new InstructionParseException("Invalid compass location: " + compass);
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        switch (action) {
            case ADD:
            case DEL:
                // Add Tag to player
                try {
                    final Instruction tagInstruction = new Instruction(BetonQuest.getInstance().getLoggerFactory().create(Instruction.class), instruction.getPackage(), null, "tag " + action.toString().toLowerCase(Locale.ROOT) + " compass-" + compass);
                    BetonQuest.getInstance().getEventFactory("tag").parseEventInstruction(tagInstruction).handle(profile);
                } catch (final InstructionParseException e) {
                    log.warn(instruction.getPackage(), "Failed to tag " + profile + " with compass point: " + compass, e);
                }
                return null;
            case SET:
                final Location location;
                try {
                    location = compassLocation.getLocation(profile);
                } catch (final QuestRuntimeException e) {
                    log.warn(instruction.getPackage(), "Failed to set compass: " + compass, e);
                    return null;
                }
                if (profile.getOnlineProfile().isPresent()) {
                    final QuestCompassTargetChangeEvent event = new QuestCompassTargetChangeEvent(profile, location);
                    Bukkit.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        profile.getOnlineProfile().get().getPlayer().setCompassTarget(location);
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
