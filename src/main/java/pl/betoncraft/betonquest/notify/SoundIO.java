package pl.betoncraft.betonquest.notify;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;

import java.util.Map;

public class SoundIO extends NotifyIO {

    public SoundIO(final Map<String, String> data) throws InstructionParseException {
        super(data);
    }

    @Override
    protected void notifyPlayer(final String message, final Player player) {
    }
}
