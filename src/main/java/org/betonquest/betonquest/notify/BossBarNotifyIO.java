package org.betonquest.betonquest.notify;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
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

    private final List<net.kyori.adventure.bossbar.BossBar.Flag> adventureBarFlags;

    private final BarColor barColor;

    private final net.kyori.adventure.bossbar.BossBar.Color adventureBarColor;

    private final BarStyle style;

    private final net.kyori.adventure.bossbar.BossBar.Overlay adventureBarStyle;

    private final float progress;

    private final VariableNumber stayVariable;

    private final int countdown;

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    public BossBarNotifyIO(final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(pack, data);
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());

        barFlags = new ArrayList<>();
        adventureBarFlags = new ArrayList<>();
        if (data.containsKey("barflags")) {
            for (final String flag : data.get("barflags").split(",")) {
                final String upperCaseFlag = flag.toUpperCase(Locale.ROOT);
                try {
                    barFlags.add(BarFlag.valueOf(upperCaseFlag));
                    adventureBarFlags.add(net.kyori.adventure.bossbar.BossBar.Flag.valueOf(upperCaseFlag));
                } catch (final IllegalArgumentException exception) {
                    throw new QuestException(String.format(CATCH_MESSAGE_TYPE, "BarFlag", upperCaseFlag), exception);
                }
            }
        }

        final String barColorString = data.get("barcolor");
        final String upperCaseBarColour = barColorString == null ? null : barColorString.toUpperCase(Locale.ROOT);
        try {
            barColor = upperCaseBarColour == null ? BarColor.BLUE : BarColor.valueOf(upperCaseBarColour);
            adventureBarColor = upperCaseBarColour == null ? net.kyori.adventure.bossbar.BossBar.Color.BLUE : net.kyori.adventure.bossbar.BossBar.Color.valueOf(upperCaseBarColour);
        } catch (final IllegalArgumentException exception) {
            throw new QuestException(String.format(CATCH_MESSAGE_TYPE, "BarColor", upperCaseBarColour), exception);
        }

        final String styleString = data.get("style");
        final String upperCaseBarStyle = styleString == null ? null : styleString.toUpperCase(Locale.ROOT);
        try {
            style = styleString == null ? BarStyle.SOLID : BarStyle.valueOf(upperCaseBarStyle);
            adventureBarStyle = styleString == null ? net.kyori.adventure.bossbar.BossBar.Overlay.PROGRESS : net.kyori.adventure.bossbar.BossBar.Overlay.valueOf(upperCaseBarStyle);
        } catch (final IllegalArgumentException exception) {
            throw new QuestException(String.format(CATCH_MESSAGE_TYPE, "BarStyle", upperCaseBarStyle), exception);
        }

        progress = normalizeBossBarProgress(getFloatData("progress", 1));
        final String stayString = data.getOrDefault("stay", "70");
        stayVariable = new VariableNumber(BetonQuest.getInstance().getVariableProcessor(), pack, stayString);
        countdown = getIntegerData("countdown", 0);
    }

    @Override
    protected void notifyPlayer(final String message, final OnlineProfile onlineProfile) {
        final BossBar bossBar = Bukkit.createBossBar(message, barColor, style);
        bossBar.setProgress(getResolvedProgress(onlineProfile));
        for (final BarFlag flag : barFlags) {
            bossBar.addFlag(flag);
        }
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

    @Override
    protected void notifyPlayer(final Component message, final OnlineProfile onlineProfile) {
        final net.kyori.adventure.bossbar.BossBar bossBar = net.kyori.adventure.bossbar.BossBar.bossBar(message, getResolvedProgress(onlineProfile), adventureBarColor, adventureBarStyle);
        for (final net.kyori.adventure.bossbar.BossBar.Flag flag : adventureBarFlags) {
            bossBar.addFlag(flag);
        }
        onlineProfile.getPlayer().showBossBar(bossBar);

        final int stay = Math.max(0, stayVariable.getInt(onlineProfile));
        scheduleRemoval(bossBar, onlineProfile, stay);
        if (countdown > 0) {
            final int interval = stay / countdown;
            final float amount = progress / countdown;
            scheduleAnimation(bossBar, interval, amount);
        }
    }

    private float getResolvedProgress(final OnlineProfile onlineProfile) {
        try {
            return normalizeBossBarProgress(getFloatData(onlineProfile.getPlayer(), "progress", 1));
        } catch (final QuestException e) {
            log.warn(pack, "Invalid variable in bossbar notification from package '" + (pack == null ? "null" : pack.getQuestPath()) + "': " + e.getMessage(), e);
        }
        return 0;
    }

    private float normalizeBossBarProgress(final float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    private void scheduleRemoval(final BossBar bar, final Integer stay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                bar.removeAll();
            }
        }.runTaskLater(BetonQuest.getInstance(), stay);
    }

    private void scheduleRemoval(final net.kyori.adventure.bossbar.BossBar bossBar, final OnlineProfile onlineProfile, final int stay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                onlineProfile.getPlayer().hideBossBar(bossBar);
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

    private void scheduleAnimation(final net.kyori.adventure.bossbar.BossBar bossBar, final int interval, final float amount) {

        new BukkitRunnable() {
            private int currentCountdown = countdown;

            private float currentProgress = progress;

            @Override
            public void run() {
                if (currentCountdown == 0) {
                    cancel();
                    return;
                }
                currentCountdown -= 1;
                currentProgress -= amount;
                bossBar.progress(Math.max(0.0F, currentProgress));
            }
        }.runTaskTimer(BetonQuest.getInstance(), interval, interval);
    }
}
