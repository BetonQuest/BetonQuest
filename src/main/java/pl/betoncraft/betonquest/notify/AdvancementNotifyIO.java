/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.betoncraft.betonquest.notify;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Use Advancement Popup for Notification
 * <p>
 * Data Valuues:
 * * frame: {challenge|goal|task|default} - What frame to use
 * * icon:  {item_name} - What icon to use
 */
public class AdvancementNotifyIO extends NotifyIO {

    private String icon;

    // Variables
    private String frame;

    public AdvancementNotifyIO(Map<String, String> data) {
        super(data);

        frame = FrameType.DEFAULT.str;
        if (getData().containsKey("frame")) {
            try {
                frame = FrameType.valueOf(getData().get("frame").toUpperCase()).str;
            } catch (IllegalArgumentException e) {
                LogUtils.logThrowableIgnore(e);
            }
        }

        icon = "minecraft:map";
        if (getData().containsKey("icon")) {
            // Before 1.13 we can't check this without linking to NMS so for now we'll have to trust the user input
            // and catch the horrible exception later.
            icon = getData().get("icon");
        }
    }

    @Override
    public void sendNotify(String message, Collection<? extends Player> players) {
        NamespacedKey id = new NamespacedKey(BetonQuest.getInstance(), "notify/" + UUID.randomUUID().toString());

        // Add the advancement. Pre 1.13 we have to catch some errors here
        try {
            add(id, Utils.format(message));
        } catch (JsonSyntaxException e) {
            LogUtils.getLogger().log(Level.WARNING, "Failed to create notification. Check your syntax and make sure your icon is lowercase with its vanilla name (IE: minecraft:map)");
            LogUtils.logThrowable(e);
            return;
        }

        // Grant to players
        for (Player player : players) {
            grant(id, player);
        }

        // Remove after 10 ticks
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : players) {
                    revoke(id, player);
                }
                remove(id);
            }
        }.runTaskLater(BetonQuest.getInstance(), 10);

        super.sendNotify(message, players);
    }

    @SuppressWarnings("deprecation")
    private void add(NamespacedKey id, String message) {
        Bukkit.getUnsafe().loadAdvancement(id, generateJson(message));
    }

    @SuppressWarnings("deprecation")
    private void remove(NamespacedKey id) {
        Bukkit.getUnsafe().removeAdvancement(id);
    }

    private void grant(NamespacedKey id, Player player) {
        Advancement advancement = Bukkit.getAdvancement(id);
        AdvancementProgress progress = player.getAdvancementProgress(advancement);
        if (!progress.isDone()) {
            for (String criteria : progress.getRemainingCriteria()) {
                progress.awardCriteria(criteria);
            }
        }
    }

    private void revoke(NamespacedKey id, Player player) {
        Advancement advancement = Bukkit.getAdvancement(id);
        AdvancementProgress progress = player.getAdvancementProgress(advancement);
        if (progress.isDone()) {
            for (String criteria : progress.getRemainingCriteria()) {
                progress.revokeCriteria(criteria);
            }
        }
    }

    private String generateJson(String message) {
        JsonObject json = new JsonObject();


        JsonObject icon = new JsonObject();
        icon.addProperty("item", this.icon);

        JsonObject display = new JsonObject();
        display.add("icon", icon);
        display.addProperty("title", message);

        display.addProperty("description", "");
        display.addProperty("background", "minecraft:textures/gui/advancements/backgrounds/adventure.png");

        display.addProperty("frame", this.frame);

        display.addProperty("announce_to_chat", false);
        display.addProperty("show_toast", true);
        display.addProperty("hidden", true);

        JsonObject criteria = new JsonObject();
        JsonObject trigger = new JsonObject();

        trigger.addProperty("trigger", "minecraft:impossible");
        criteria.add("impossible", trigger);

        json.add("criteria", criteria);
        json.add("display", display);

        return new GsonBuilder().setPrettyPrinting().create().toJson(json);
    }


    public static enum FrameType {
        CHALLENGE("challenge"),
        GOAL("goal"),
        TASK("task"),
        DEFAULT("challenge");

        private String str;

        FrameType(String str) {
            this.str = str;
        }

        public String getName() {
            return this.str;
        }
    }
}
