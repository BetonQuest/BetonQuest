package org.betonquest.betonquest.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;

import java.util.Locale;

/**
 * Player needs to get specified experience level or more.
 */
@SuppressWarnings("PMD.CommentRequired")
public class ExperienceObjective extends Objective implements Listener {

    /**
     * The experience level the player needs to get.
     * The decimal part of the number is a percentage of the next level.
     */
    private final VariableNumber amount;

    public ExperienceObjective(final Instruction instruction) throws QuestException {
        super(instruction);
        this.amount = instruction.get(VariableNumber::new);
    }

    private void onExperienceChange(final OnlineProfile onlineProfile, final double newAmount, final boolean notify) {
        if (!containsPlayer(onlineProfile)) {
            return;
        }
        final double amount = this.amount.getDouble(onlineProfile);
        if (newAmount >= amount) {
            if (checkConditions(onlineProfile)) {
                completeObjective(onlineProfile);
            }
        } else if (this.notify && notify) {
            final int level = (int) (amount - newAmount);
            if (level % notifyInterval == 0) {
                sendNotify(onlineProfile, "level_to_gain", level);
            }
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void start(final Profile profile) {
        super.start(profile);
        profile.getOnlineProfile()
                .ifPresent(onlineProfile -> {
                    final Player player = onlineProfile.getPlayer();
                    final double newAmount = player.getLevel() + player.getExp();
                    onExperienceChange(onlineProfile, newAmount, false);
                });
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "amount" -> profile.getOnlineProfile()
                    .map(OnlineProfile::getPlayer)
                    .map(player -> player.getLevel() + player.getExp())
                    .map(String::valueOf)
                    .orElse("");
            case "left" -> {
                final double pAmount = amount.getDouble(profile);
                yield profile.getOnlineProfile()
                        .map(OnlineProfile::getPlayer)
                        .map(player -> player.getLevel() + player.getExp())
                        .map(exp -> pAmount - exp)
                        .map(String::valueOf)
                        .orElse("");
            }
            case "total" -> String.valueOf(amount.getDouble(profile));
            default -> "";
        };
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLevelChangeEvent(final PlayerLevelChangeEvent event) {
        final Player player = event.getPlayer();
        final double newAmount = player.getLevel() + player.getExp();
        onExperienceChange(PlayerConverter.getID(player), newAmount, true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onExpChangeEvent(final PlayerExpChangeEvent event) {
        final Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(BetonQuest.getInstance(), () -> {
            final double newAmount = player.getLevel() + player.getExp();
            onExperienceChange(PlayerConverter.getID(player), newAmount, false);
        });
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final OnlineProfile onlineProfile = PlayerConverter.getID(player);
        final double newAmount = player.getLevel() + player.getExp();
        onExperienceChange(onlineProfile, newAmount, false);
    }
}
