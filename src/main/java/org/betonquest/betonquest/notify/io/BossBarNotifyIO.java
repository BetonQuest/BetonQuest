package org.betonquest.betonquest.notify.io;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.notify.NotifyIO;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Displays the message as boss bar.
 */
public class BossBarNotifyIO extends NotifyIO {
    /**
     * The flags for the boss bar.
     */
    private final List<BossBar.Flag> flags;

    /**
     * The color of the boss bar.
     */
    private final BossBar.Color color;

    /**
     * The style of the boss bar.
     */
    private final BossBar.Overlay style;

    /**
     * The variable for the progress of the boss bar.
     */
    private final Variable<Number> variableProgress;

    /**
     * The variable for the time the boss bar should stay visible.
     */
    private final Variable<Number> variableStay;

    /**
     * The countdown variable for the boss bar.
     */
    private final Variable<Number> variableCountdown;

    /**
     * Create a new Boss Bar Notify IO.
     *
     * @param pack the source pack to resolve variables
     * @param data the customization data for notifications
     * @throws QuestException when data could not be parsed
     */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public BossBarNotifyIO(@Nullable final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(pack, data);

        flags = new ArrayList<>();
        if (data.containsKey("barflags")) {
            for (final String flag : data.get("barflags").split(",")) {
                final String upperCaseFlag = flag.toUpperCase(Locale.ROOT);
                try {
                    flags.add(BossBar.Flag.valueOf(upperCaseFlag));
                } catch (final IllegalArgumentException exception) {
                    throw new QuestException(String.format(CATCH_MESSAGE_TYPE, "BarFlag", upperCaseFlag), exception);
                }
            }
        }

        final String barColorString = data.get("barcolor");
        final String upperCaseBarColor = barColorString == null ? null : barColorString.toUpperCase(Locale.ROOT);
        try {
            color = upperCaseBarColor == null ? BossBar.Color.BLUE : BossBar.Color.valueOf(upperCaseBarColor);
        } catch (final IllegalArgumentException exception) {
            throw new QuestException(String.format(CATCH_MESSAGE_TYPE, "BarColor", upperCaseBarColor), exception);
        }

        final String styleString = data.get("style");
        final String upperCaseBarStyle = styleString == null ? null : styleString.toUpperCase(Locale.ROOT);
        try {
            style = styleString == null ? BossBar.Overlay.PROGRESS : BossBar.Overlay.valueOf(upperCaseBarStyle);
        } catch (final IllegalArgumentException exception) {
            throw new QuestException(String.format(CATCH_MESSAGE_TYPE, "BarStyle", upperCaseBarStyle), exception);
        }

        variableProgress = getNumberData("progress", 1);
        variableStay = getNumberData("stay", 70);
        variableCountdown = getNumberData("countdown", 0);
    }

    @Override
    protected void notifyPlayer(final Component message, final OnlineProfile onlineProfile) throws QuestException {
        final float progress = Math.max(0.0F, Math.min(1.0F, variableProgress.getValue(onlineProfile).floatValue()));
        final int countdown = variableCountdown.getValue(onlineProfile).intValue();
        final int stay = Math.max(0, variableStay.getValue(onlineProfile).intValue());

        final BossBar bossBar = BossBar.bossBar(message, progress, color, style);
        for (final BossBar.Flag flag : flags) {
            bossBar.addFlag(flag);
        }
        onlineProfile.getPlayer().showBossBar(bossBar);

        scheduleRemoval(bossBar, onlineProfile, stay);
        if (countdown > 0) {
            final int interval = stay / countdown;
            final float amount = progress / countdown;
            scheduleAnimation(bossBar, interval, amount, countdown, progress);
        }
    }

    private void scheduleRemoval(final BossBar bossBar, final OnlineProfile onlineProfile, final int stay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                onlineProfile.getPlayer().hideBossBar(bossBar);
            }
        }.runTaskLater(BetonQuest.getInstance(), stay);
    }

    private void scheduleAnimation(final BossBar bossBar, final int interval, final float amount,
                                   final int startCountdown, final float startProgress) {
        new BukkitRunnable() {
            /**
             * The current countdown value.
             */
            private int currentCountdown = startCountdown;

            /**
             * The current progress of the boss bar.
             * Starts at the initial progress and decreases by the specified amount each tick.
             */
            private float currentProgress = startProgress;

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
