package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class BossBarNotifyIO extends NotifyIO {

    private final List<BarFlag> barFlags;
    private final BarColor barColor;
    private final BarStyle style;
    private final double progress;
    private final int stay;
    private final int countdown;

    @SuppressWarnings("PMD.CyclomaticComplexity")
    public BossBarNotifyIO(final Map<String, String> data) throws InstructionParseException {
        super(data);

        barFlags = new ArrayList<>();
        if (data.containsKey("barflags")) {
            for (final String flag : data.get("barflags").split(",")) {
                try {
                    barFlags.add(BarFlag.valueOf(flag.toUpperCase(Locale.ROOT)));
                } catch (final IllegalArgumentException exception) {
                    throw new InstructionParseException(String.format(CATCH_MESSAGE_TYPE, "BarFlag", flag.toUpperCase(Locale.ROOT)), exception);
                }
            }
        }

        final String barColorString = data.get("barcolor");
        try {
            barColor = barColorString == null ? BarColor.BLUE : BarColor.valueOf(barColorString.toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException exception) {
            throw new InstructionParseException(String.format(CATCH_MESSAGE_TYPE, "BarColor", barColorString.toUpperCase(Locale.ROOT)), exception);
        }

        final String styleString = data.get("style");
        try {
            style = styleString == null ? BarStyle.SOLID : BarStyle.valueOf(styleString.toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException exception) {
            throw new InstructionParseException(String.format(CATCH_MESSAGE_TYPE, "BarStyle", styleString.toUpperCase(Locale.ROOT)), exception);
        }

        progress = Math.max(0.0, Math.min(1.0, getFloatData("progress", 1)));
        stay = Math.max(0, getIntegerData("stay", 70));
        countdown = getIntegerData("countdown", 0);
    }

    @Override
    protected void notifyPlayer(final String message, final Player player) {
        final BossBar bossBar = Bukkit.createBossBar(message, barColor, style);
        for (final BarFlag flag : barFlags) {
            bossBar.addFlag(flag);
        }
        bossBar.setProgress(progress);
        bossBar.addPlayer(player);
        bossBar.setVisible(true);
        scheduleRemoval(bossBar);

        if (countdown > 0) {
            final int interval = stay / countdown;
            final double amount = progress / ((double) countdown);
            scheduleAnimation(bossBar, interval, amount);
        }
    }

    private void scheduleRemoval(final BossBar bar) {
        new BukkitRunnable() {
            @Override
            public void run() {
                bar.removeAll();
            }
        }.runTaskLater(BetonQuest.getInstance(), stay);
    }

    private void scheduleAnimation(final BossBar bar, final int interval, final double amount) {

        new BukkitRunnable() {
            private int currentCountdown = countdown;
            private double currentProgress = progress;

            @Override
            public void run() {
                if (currentCountdown == 0) {
                    cancel();
                    return;
                }
                currentCountdown -= 1;
                currentProgress -= amount;
                bar.setProgress(Math.max(0.0, currentProgress));

            }
        }.runTaskTimer(BetonQuest.getInstance(), interval, interval);
    }
}
