package pl.betoncraft.betonquest.notify;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to supress any notification including sounds
 */
public class SuppressNotifyIO extends NotifyIO {

    public SuppressNotifyIO(final Map<String, String> data) {
        super(data);
    }

    @Override
    public void sendNotify(final HashMap<Player, String> playerMessages) {
    }
}
