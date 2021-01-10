package pl.betoncraft.betonquest.events;

import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.location.CompoundLocation;

import java.util.logging.Level;

/**
 * Plays a sound for the player
 *
 * @deprecated Use the {@link NotifyEvent} instead,
 * this will be removed in 2.0 release
 */
// TODO Delete in BQ 2.0.0
@Deprecated
@SuppressWarnings("PMD.CommentRequired")
public class PlaysoundEvent extends QuestEvent {

    private final String sound;
    private final CompoundLocation location;
    private final SoundCategory soundCategoty;
    private final float volume;
    private final float pitch;

    public PlaysoundEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        LogUtils.getLogger().log(Level.WARNING, "Playsound event will be REMOVED! Usage in package '"
                + instruction.getPackage().getName() + "'. Use the Notify system instead: "
                + "https://betonquest.github.io/BetonQuest/RELEASE/User-Documentation/Notification-Settings/");
        sound = instruction.next();
        location = instruction.getLocation(instruction.getOptional("location"));
        final String category = instruction.getOptional("category");
        if (category == null) {
            soundCategoty = SoundCategory.MASTER;
        } else {
            soundCategoty = instruction.getEnum(category, SoundCategory.class);
        }
        volume = (float) instruction.getDouble(instruction.getOptional("volume"), 1D);
        pitch = (float) instruction.getDouble(instruction.getOptional("pitch"), 1D);
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        if (location == null) {
            player.playSound(player.getLocation(), sound, soundCategoty, volume, pitch);
        } else {
            player.playSound(location.getLocation(playerID), sound, soundCategoty, volume, pitch);
        }
        return null;
    }
}
