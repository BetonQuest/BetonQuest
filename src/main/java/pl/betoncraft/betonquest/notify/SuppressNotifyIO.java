package pl.betoncraft.betonquest.notify;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

/**
 * Used to supress any notification including sounds
 */
public class SuppressNotifyIO extends NotifyIO {

    public SuppressNotifyIO(Map<String, String> data) {
        super(data);
    }

    @Override
    public void sendNotify(String message, Collection<? extends Player> players) {
    }
}
