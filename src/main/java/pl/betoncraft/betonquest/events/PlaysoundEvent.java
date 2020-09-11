package pl.betoncraft.betonquest.events;

import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Plays a sound for the player
 */
public class PlaysoundEvent extends QuestEvent {

    private final String sound;
    private final LocationData location;
    private final SoundCategory soundCategoty;
    private final float volume;
    private final float pitch;

    public PlaysoundEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
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
