package pl.betoncraft.betonquest.notify;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * Use Title Popup for Notification
 *
 * Data Valuues:
 *   * fadeIn: seconds to fade in
 *   * stay: seconds to stay
 *   * fadeOut: seconds to fade out
 *   * subTitle: the subtitle to show, else blank
 *
 *
 */
public class TitleNotifyIO extends NotifyIO {


    // Variables

    private int fadeIn;
    private int stay;
    private int fadeOut;
    private String subTitle;


    public TitleNotifyIO(Map<String, String> data) {
        super(data);

        fadeIn = Integer.valueOf(data.getOrDefault("fadein", "10"));
        stay = Integer.valueOf(data.getOrDefault("stay", "70"));
        fadeOut = Integer.valueOf(data.getOrDefault("fadeout", "20"));
        subTitle = getData().getOrDefault("subtitle", "").replace("_", " ");
    }

    @Override
    public void sendNotify(String message, Collection<? extends Player> players) {
        for (Player player : players) {
            player.sendTitle(message, subTitle, fadeIn, stay, fadeOut);
        }

        super.sendNotify(message, players);
    }
}
