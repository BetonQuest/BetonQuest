package org.betonquest.betonquest.notify;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class AdvancementNotifyIO extends NotifyIO {

    private final String frame;
    private final String icon;

    public AdvancementNotifyIO(final Map<String, String> data) throws InstructionParseException {
        super(data);

        frame = data.getOrDefault("frame", "challenge").toLowerCase(Locale.ROOT);
        icon = data.getOrDefault("icon", "minecraft:map").toLowerCase(Locale.ROOT);
    }

    @Override
    protected void notifyPlayer(final String message, final Player player) {
        final NamespacedKey key = loadAdvancement(message);
        grant(key, player);
        new BukkitRunnable() {
            @Override
            public void run() {
                revoke(key, player);
                remove(key);
            }
        }.runTaskLater(BetonQuest.getInstance(), 10);
    }

    private NamespacedKey loadAdvancement(final String message) {
        final NamespacedKey key = new NamespacedKey(BetonQuest.getInstance(), "notify/" + UUID.randomUUID().toString());
        try {
            add(key, message);
        } catch (final JsonIOException e) {
            LOG.warning(null, "Failed to create notification with text: '" + message + "'! Cause: " + e.getMessage(), e);
        }
        return key;
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
        if (advancement != null) {
            final AdvancementProgress progress = player.getAdvancementProgress(advancement);
            if (!progress.isDone()) {
                for (final String criteria : progress.getRemainingCriteria()) {
                    progress.awardCriteria(criteria);
                }
            }
        }
    }

    private void revoke(final NamespacedKey key, final Player player) {
        final Advancement advancement = Bukkit.getAdvancement(key);
        if (advancement != null) {
            final AdvancementProgress progress = player.getAdvancementProgress(advancement);
            if (progress.isDone()) {
                for (final String criteria : progress.getRemainingCriteria()) {
                    progress.revokeCriteria(criteria);
                }
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
}
