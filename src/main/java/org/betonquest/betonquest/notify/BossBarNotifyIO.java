package org.betonquest.betonquest.notify;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
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

    private final List<BossBar.Flag> flags;

    private final BossBar.Color color;

    private final BossBar.Overlay style;

    private final float progress;

    private final VariableNumber stayVariable;

    private final int countdown;

    @SuppressWarnings("PMD.CyclomaticComplexity")
    public BossBarNotifyIO(final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(pack, data);
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());

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
        final String upperCaseBarColour = barColorString == null ? null : barColorString.toUpperCase(Locale.ROOT);
        try {
            color = upperCaseBarColour == null ? BossBar.Color.BLUE : BossBar.Color.valueOf(upperCaseBarColour);
        } catch (final IllegalArgumentException exception) {
            throw new QuestException(String.format(CATCH_MESSAGE_TYPE, "BarColor", upperCaseBarColour), exception);
        }

        final String styleString = data.get("style");
        final String upperCaseBarStyle = styleString == null ? null : styleString.toUpperCase(Locale.ROOT);
        try {
            style = styleString == null ? BossBar.Overlay.PROGRESS : BossBar.Overlay.valueOf(upperCaseBarStyle);
        } catch (final IllegalArgumentException exception) {
            throw new QuestException(String.format(CATCH_MESSAGE_TYPE, "BarStyle", upperCaseBarStyle), exception);
        }

        progress = normalizeBossBarProgress(getFloatData("progress", 1));
        final String stayString = data.getOrDefault("stay", "70");
        stayVariable = new VariableNumber(BetonQuest.getInstance().getVariableProcessor(), pack, stayString);
        countdown = getIntegerData("countdown", 0);
    }

    @Override
    protected void notifyPlayer(final Component message, final OnlineProfile onlineProfile) {
        final BossBar bossBar = BossBar.bossBar(message, getResolvedProgress(onlineProfile), color, style);
        for (final BossBar.Flag flag : flags) {
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

    private void scheduleRemoval(final BossBar bossBar, final OnlineProfile onlineProfile, final int stay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                onlineProfile.getPlayer().hideBossBar(bossBar);
            }
        }.runTaskLater(BetonQuest.getInstance(), stay);
    }

    private void scheduleAnimation(final BossBar bossBar, final int interval, final float amount) {

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
