package org.betonquest.betonquest.notify.io;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.notify.NotifyIO;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Displays the message as advancement.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class AdvancementNotifyIO extends NotifyIO {

    /**
     * Plugin to start tasks.
     */
    private final Plugin plugin;

    /**
     * Advancement frame.
     */
    private final String frame;

    /**
     * Advancement icon.
     */
    private final String icon;

    /**
     * Create a new Advancement Notify IO.
     *
     * @param variables the variable processor to create and resolve variables
     * @param pack      the source pack to resolve variables
     * @param data      the customization data for notifications
     * @param plugin    the plugin to start tasks
     * @throws QuestException when data could not be parsed
     */
    public AdvancementNotifyIO(final Variables variables, @Nullable final QuestPackage pack, final Map<String, String> data, final Plugin plugin) throws QuestException {
        super(variables, pack, data);
        this.plugin = plugin;
        frame = data.getOrDefault("frame", "challenge").toLowerCase(Locale.ROOT);
        icon = data.getOrDefault("icon", "minecraft:map").toLowerCase(Locale.ROOT);
    }

    private void notifyPlayerObject(final JsonElement message, final OnlineProfile onlineProfile) {
        final UUID uuid = UUID.randomUUID();
        final NamespacedKey rootKey = new NamespacedKey(plugin, "notify/" + uuid + "-root");
        final NamespacedKey key = new NamespacedKey(plugin, "notify/" + uuid + "-message");
        loadAdvancement(message, rootKey, key);

        Bukkit.getScheduler().runTask(plugin, run -> grant(key, onlineProfile.getPlayer()));
        new BukkitRunnable() {
            @Override
            public void run() {
                revoke(key, onlineProfile.getPlayer());
                remove(key);
                remove(rootKey);
            }
        }.runTaskLater(plugin, 10);
    }

    @Override
    protected void notifyPlayer(final Component message, final OnlineProfile onlineProfile) {
        notifyPlayerObject(GsonComponentSerializer.gson().serializeToTree(message), onlineProfile);
    }

    @SuppressWarnings("deprecation")
    private void loadAdvancement(final JsonElement message, final NamespacedKey rootKey, final NamespacedKey key) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getUnsafe().loadAdvancement(rootKey, generateJson(null, null));
                Bukkit.getUnsafe().loadAdvancement(key, generateJson(message, rootKey));
            }
        }.runTask(plugin);
    }

    @SuppressWarnings("deprecation")
    private void remove(final NamespacedKey key) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getUnsafe().removeAdvancement(key);
            }
        }.runTask(plugin);
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

    private String generateJson(@Nullable final JsonElement message, @Nullable final NamespacedKey root) {
        final JsonObject json = new JsonObject();
        json.add("criteria", getCriteria());
        if (root != null) {
            json.addProperty("parent", root.toString());
            json.add("display", getDisplay(message));
        }
        return new GsonBuilder().setPrettyPrinting().create().toJson(json);
    }

    private JsonObject getCriteria() {
        final JsonObject criteria = new JsonObject();
        criteria.add("impossible", getTrigger());
        return criteria;
    }

    private JsonObject getTrigger() {
        final JsonObject trigger = new JsonObject();
        trigger.addProperty("trigger", "minecraft:impossible");
        return trigger;
    }

    private JsonObject getDisplay(@Nullable final JsonElement message) {
        final JsonObject display = new JsonObject();
        display.add("icon", getIcon());
        display.add("title", message);
        display.addProperty("description", "");
        display.addProperty("frame", this.frame);
        display.addProperty("announce_to_chat", false);
        display.addProperty("show_toast", true);
        display.addProperty("hidden", true);
        return display;
    }

    private JsonObject getIcon() {
        final JsonObject icon = new JsonObject();
        if (PaperLib.isVersion(20, 5)) {
            icon.addProperty("id", this.icon);
        } else {
            icon.addProperty("item", this.icon);
        }
        return icon;
    }
}
