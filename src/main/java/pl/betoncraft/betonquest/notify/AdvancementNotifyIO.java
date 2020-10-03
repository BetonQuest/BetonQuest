package pl.betoncraft.betonquest.notify;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.*;
import java.util.logging.Level;

/**
 * Use Advancement Popup for Notification
 * <p>
 * Data Values:
 * * frame: {challenge|goal|task|default} - What frame to use
 * * icon:  {item_name} - What icon to use
 */
public class AdvancementNotifyIO extends NotifyIO {

    private String icon;

    // Variables
    private String frame;

    public AdvancementNotifyIO(final Map<String, String> data) {
        super(data);

        frame = FrameType.DEFAULT.str;
        if (getData().containsKey("frame")) {
            try {
                frame = FrameType.valueOf(getData().get("frame").toUpperCase(Locale.ROOT)).str;
            } catch (IllegalArgumentException e) {
                LogUtils.logThrowableIgnore(e);
            }
        }

        icon = "minecraft:map";
        if (getData().containsKey("icon")) {
            icon = getData().get("icon").toLowerCase();
        }
    }

    @Override
    public void sendNotify(final HashMap<Player, String> playerMessages) {
        final HashMap<String, Pair<ArrayList<Player>, NamespacedKey>> messages = loadAdvancements(playerMessages);

        grantAll(messages);

        // Remove after 10 ticks
        new BukkitRunnable() {

            @Override
            public void run() {
                revokeAll(messages);
                removeAll(messages);
            }
        }.runTaskLater(BetonQuest.getInstance(), 10);

        sendNotificationSound(playerMessages.keySet());
    }

    private HashMap<String, Pair<ArrayList<Player>, NamespacedKey>> loadAdvancements(final HashMap<Player, String> playerMessages) {
        final HashMap<String, Pair<ArrayList<Player>, NamespacedKey>> messages = new HashMap<>();
        for (final Map.Entry<Player, String> entry : playerMessages.entrySet()) {
            if (messages.containsKey(entry.getValue())) {
                messages.get(entry.getValue()).getLeft().add(entry.getKey());
            } else {
                final ArrayList<Player> players = new ArrayList<>();
                players.add(entry.getKey());
                final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "notify/" + UUID.randomUUID().toString());
                try {
                    add(key, Utils.format(entry.getValue()));
                } catch (Exception e) {
                    LogUtils.getLogger().log(Level.WARNING, "Failed to create notification: '" + entry.getValue() + "',! Cause: " + e.getMessage());
                    LogUtils.logThrowable(e);
                    messages.put(entry.getValue(), Pair.of(players, null));
                    continue;
                }
                messages.put(entry.getValue(), Pair.of(players, key));
            }
        }
        return messages;
    }

    private void grantAll(final HashMap<String, Pair<ArrayList<Player>, NamespacedKey>> messages) {
        for(final Pair<ArrayList<Player>, NamespacedKey> entry : messages.values()) {
            if(entry.getRight() != null) {
                for(final Player player : entry.getLeft()) {
                    grant(entry.getRight(), player);
                }
            }
        }
    }

    private void revokeAll(final HashMap<String, Pair<ArrayList<Player>, NamespacedKey>> messages) {
        for(final Pair<ArrayList<Player>, NamespacedKey> entry : messages.values()) {
            if(entry.getRight() != null) {
                for(final Player player : entry.getLeft()) {
                    revoke(entry.getRight(), player);
                }
            }
        }
    }

    private void removeAll(final HashMap<String, Pair<ArrayList<Player>, NamespacedKey>> messages) {
        for(final Pair<ArrayList<Player>, NamespacedKey> entry : messages.values()) {
            if(entry.getRight() != null) {
                remove(entry.getRight());
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void add(final NamespacedKey key, final String message) {
        Bukkit.getUnsafe().loadAdvancement(key, generateJson(message));
    }

    @SuppressWarnings("deprecation")
    private void remove(final NamespacedKey key) {
        Bukkit.getUnsafe().removeAdvancement(key);
    }

    private void grant(final NamespacedKey key, final Player player) {
        final Advancement advancement = Bukkit.getAdvancement(key);
        final AdvancementProgress progress = player.getAdvancementProgress(advancement);
        if (!progress.isDone()) {
            for (final String criteria : progress.getRemainingCriteria()) {
                progress.awardCriteria(criteria);
            }
        }
    }

    private void revoke(final NamespacedKey key, final Player player) {
        final Advancement advancement = Bukkit.getAdvancement(key);
        final AdvancementProgress progress = player.getAdvancementProgress(advancement);
        if (progress.isDone()) {
            for (final String criteria : progress.getRemainingCriteria()) {
                progress.revokeCriteria(criteria);
            }
        }
    }

    private String generateJson(final String message) {
        final JsonObject json = new JsonObject();


        final JsonObject icon = new JsonObject();
        icon.addProperty("item", this.icon);

        final JsonObject display = new JsonObject();
        display.add("icon", icon);
        display.addProperty("title", message);

        display.addProperty("description", "");
        display.addProperty("background", "minecraft:textures/gui/advancements/backgrounds/adventure.png");

        display.addProperty("frame", this.frame);

        display.addProperty("announce_to_chat", false);
        display.addProperty("show_toast", true);
        display.addProperty("hidden", true);

        final JsonObject criteria = new JsonObject();
        final JsonObject trigger = new JsonObject();

        trigger.addProperty("trigger", "minecraft:impossible");
        criteria.add("impossible", trigger);

        json.add("criteria", criteria);
        json.add("display", display);

        return new GsonBuilder().setPrettyPrinting().create().toJson(json);
    }


    public enum FrameType {
        CHALLENGE("challenge"),
        GOAL("goal"),
        TASK("task"),
        DEFAULT("challenge");

        private final String str;

        FrameType(final String str) {
            this.str = str;
        }

        public String getName() {
            return this.str;
        }
    }
}
