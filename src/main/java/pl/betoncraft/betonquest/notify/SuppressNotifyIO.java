package pl.betoncraft.betonquest.notify;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;

import java.util.HashMap;
import java.util.Map;

public class SuppressNotifyIO extends NotifyIO {

    public SuppressNotifyIO(final Map<String, String> data) throws InstructionParseException {
        super(new HashMap<>());
    }

    @Override
    protected void sendNotify(final String message, final Player player) {
    }
}
