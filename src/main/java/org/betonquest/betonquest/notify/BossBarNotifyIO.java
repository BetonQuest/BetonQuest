package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
public class BossBarNotifyIO extends NotifyIO {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    private final List<BarFlag> barFlags;

    private final BarColor barColor;

    private final BarStyle style;

    private final double progress;

    private final VariableNumber stayVariable;

    private final int countdown;

    @SuppressWarnings("PMD.CyclomaticComplexity")
    public BossBarNotifyIO(final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(pack, data);
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());

        barFlags = new ArrayList<>();
        if (data.containsKey("barflags")) {
            for (final String flag : data.get("barflags").split(",")) {
                try {
                    barFlags.add(BarFlag.valueOf(flag.toUpperCase(Locale.ROOT)));
                } catch (final IllegalArgumentException exception) {
                    throw new QuestException(String.format(CATCH_MESSAGE_TYPE, "BarFlag", flag.toUpperCase(Locale.ROOT)), exception);
                }
            }
        }

        final String barColorString = data.get("barcolor");
        try {
            barColor = barColorString == null ? BarColor.BLUE : BarColor.valueOf(barColorString.toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException exception) {
            throw new QuestException(String.format(CATCH_MESSAGE_TYPE, "BarColor", barColorString.toUpperCase(Locale.ROOT)), exception);
        }

        final String styleString = data.get("style");
        try {
            style = styleString == null ? BarStyle.SOLID : BarStyle.valueOf(styleString.toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException exception) {
            throw new QuestException(String.format(CATCH_MESSAGE_TYPE, "BarStyle", styleString.toUpperCase(Locale.ROOT)), exception);
        }

        progress = normalizeBossBarProgress(getFloatData("progress", 1));
        final String stayString = data.get("stay");
        stayVariable = stayString == null ? new VariableNumber(pack, "70") : new VariableNumber(pack, stayString);
        countdown = getIntegerData("countdown", 0);
    }

    @Override
    protected void notifyPlayer(final String message, final OnlineProfile onlineProfile) {
        final BossBar bossBar = Bukkit.createBossBar(message, barColor, style);
        for (final BarFlag flag : barFlags) {
            bossBar.addFlag(flag);
        }
        double resolvedProgress = 0;
        try {
            resolvedProgress = normalizeBossBarProgress(getFloatData(onlineProfile.getPlayer(), "progress", 1));
        } catch (final QuestException e) {
            log.warn(pack, "Invalid variable in bossbar notification from package " + pack.getQuestPath() + ": " + e.getMessage(), e);
        }
        bossBar.setProgress(resolvedProgress);
        bossBar.addPlayer(onlineProfile.getPlayer());
        bossBar.setVisible(true);

        final int stay = Math.max(0, stayVariable.getInt(onlineProfile));

        scheduleRemoval(bossBar, stay);
        if (countdown > 0) {
            final int interval = stay / countdown;
            final double amount = progress / countdown;
            scheduleAnimation(bossBar, interval, amount);
        }
    }

    private double normalizeBossBarProgress(final double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private void scheduleRemoval(final BossBar bar, final Integer stay) {
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
