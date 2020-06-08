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

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Use a BossBar for Notification
 * <p>
 * Data Values:
 * * barFlags:{create_fog,darken_sky,play_boss_music} - Comma seperated BAR values
 * * barColor: {blue|green|pink|purple|red|white}
 * * progress: Progress between 0.0 and 1.0
 * * style: {segmented_10|segmented_12|segmented_20|segmented_6|solid} - Style of bar
 * * stay: ticks to stay
 */
public class BossBarNotifyIO extends NotifyIO {


    // Variables
    private List<BarFlag> barFlags = null;
    private BarColor barColor = BarColor.BLUE;
    private double progress = 1;
    private BarStyle style = BarStyle.SOLID;
    private int stay = 70;
    private int countdown = 0;

    public BossBarNotifyIO(Map<String, String> data) {
        super(data);

        if (getData().containsKey("barflags")) {
            barFlags = new ArrayList<>();
            for (String flag : getData().get("barflags").split(",")) {
                try {
                    barFlags.add(BarFlag.valueOf(flag.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Invalid BossBar barFlag: " + flag);
                    LogUtils.logThrowable(e);
                }
            }
        }

        if (getData().containsKey("barcolor")) {
            try {
                barColor = BarColor.valueOf(getData().get("barcolor").toUpperCase());
            } catch (IllegalArgumentException e) {
                LogUtils.getLogger().log(Level.WARNING, "Invalid BossBar color: " + getData().get("barcolor"));
                LogUtils.logThrowable(e);
            }
        }

        if (getData().containsKey("progress")) {
            try {
                progress = Math.max(0.0, Math.min(1.0, Double.valueOf(getData().get("progress"))));
            } catch (IllegalArgumentException e) {
                LogUtils.getLogger().log(Level.WARNING, "Invalid BossBar progress: " + getData().get("progress"));
                LogUtils.logThrowable(e);
            }
        }

        if (getData().containsKey("style")) {
            try {
                style = BarStyle.valueOf(getData().get("style").toUpperCase());
            } catch (IllegalArgumentException e) {
                LogUtils.getLogger().log(Level.WARNING, "Invalid BossBar style: " + getData().get("style"));
                LogUtils.logThrowable(e);
            }
        }

        if (getData().containsKey("stay")) {
            try {
                stay = Math.max(0, Integer.valueOf(getData().get("stay")));
            } catch (IllegalArgumentException e) {
                LogUtils.getLogger().log(Level.WARNING, "Invalid BossBar stay: " + getData().get("stay"));
                LogUtils.logThrowable(e);
            }
        }

        if (getData().containsKey("countdown")) {
            try {
                countdown = Integer.valueOf(getData().get("countdown"));
            } catch (IllegalArgumentException e) {
                LogUtils.getLogger().log(Level.WARNING, "Invalid BossBar countdown: " + getData().get("countdown"));
                LogUtils.logThrowable(e);
            }
        }

    }

    @Override
    public void sendNotify(String message, Collection<? extends Player> players) {
        BossBar bossBar = Bukkit.createBossBar(Utils.format(message), barColor, style);
        if (barFlags != null) {
            for (BarFlag flag : barFlags) {
                bossBar.addFlag(flag);
            }
        }
        bossBar.setProgress(progress);

        // Show bar
        for (Player player : players) {
            bossBar.addPlayer(player);
        }

        bossBar.setVisible(true);

        // Remove after stay ticks
        new BukkitRunnable() {

            @Override
            public void run() {
                bossBar.removeAll();
            }
        }.runTaskLater(BetonQuest.getInstance(), stay);

        // If Countdown, then divide stay by countdown and reduce progress to 0 by those intevals
        if (countdown > 0) {
            int interval = stay / countdown;
            double amount = progress / ((double) countdown);
            new BukkitRunnable() {

                @Override
                public void run() {
                    if (countdown == 0) {
                        cancel();
                        return;
                    }
                    countdown -= 1;
                    progress -= amount;
                    bossBar.setProgress(Math.max(0.0, progress));
                }
            }.runTaskTimer(BetonQuest.getInstance(), interval, interval);
        }

        super.sendNotify(message, players);
    }
}
