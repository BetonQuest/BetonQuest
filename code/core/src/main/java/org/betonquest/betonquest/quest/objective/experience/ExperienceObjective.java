package org.betonquest.betonquest.quest.objective.experience;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.quest.event.IngameNotificationSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;

import java.util.Locale;

/**
 * Player needs to get specified experience level or more.
 */
public class ExperienceObjective extends DefaultObjective implements Listener {

    /**
     * The experience level the player needs to get.
     * The decimal part of the number is a percentage of the next level.
     */
    private final Argument<Number> amount;

    /**
     * The notification to send when the player gains experience.
     */
    private final IngameNotificationSender levelSender;

    /**
     * Constructor for the ExperienceObjective.
     *
     * @param instruction the instruction that created this objective
     * @param amount      the experience level the player needs to get
     * @param levelSender the notification to send when the player gains experience
     * @throws QuestException if there is an error in the instruction
     */
    public ExperienceObjective(final Instruction instruction, final Argument<Number> amount, final IngameNotificationSender levelSender) throws QuestException {
        super(instruction);
        this.amount = amount;
        this.levelSender = levelSender;
    }

    private void onExperienceChange(final OnlineProfile onlineProfile, final double newAmount, final boolean notify) throws QuestException {
        if (!containsPlayer(onlineProfile)) {
            return;
        }
        final double amount = this.amount.getValue(onlineProfile).doubleValue();
        if (newAmount >= amount) {
            if (checkConditions(onlineProfile)) {
                completeObjective(onlineProfile);
            }
        } else if (this.notify && notify) {
            final int level = (int) (amount - newAmount);
            if (level % notifyInterval == 0) {
                levelSender.sendNotification(onlineProfile, new VariableReplacement("amount", Component.text(level)));
            }
        }
    }

    @Override
    public void start(final Profile profile) {
        super.start(profile);
        profile.getOnlineProfile()
                .ifPresent(onlineProfile -> {
                    final Player player = onlineProfile.getPlayer();
                    final double newAmount = player.getLevel() + player.getExp();
                    qeHandler.handle(() -> onExperienceChange(onlineProfile, newAmount, false));
                });
    }

    @Override
    public String getDefaultDataInstruction(final Profile profile) {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) throws QuestException {
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "amount" -> profile.getOnlineProfile()
                    .map(OnlineProfile::getPlayer)
                    .map(player -> player.getLevel() + player.getExp())
                    .map(String::valueOf)
                    .orElse("");
            case "left" -> {
                final double pAmount = amount.getValue(profile).doubleValue();
                yield profile.getOnlineProfile()
                        .map(OnlineProfile::getPlayer)
                        .map(player -> player.getLevel() + player.getExp())
                        .map(exp -> pAmount - exp)
                        .map(String::valueOf)
                        .orElse("");
            }
            case "total" -> String.valueOf(amount.getValue(profile).doubleValue());
            default -> "";
        };
    }

    /**
     * Checks the players experience when they level up.
     *
     * @param event the event that triggered this method
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLevelChangeEvent(final PlayerLevelChangeEvent event) {
        final Player player = event.getPlayer();
        final double newAmount = player.getLevel() + player.getExp();
        qeHandler.handle(() -> onExperienceChange(profileProvider.getProfile(player), newAmount, true));
    }

    /**
     * Checks the players experience when they gain experience.
     *
     * @param event the event that triggered this method
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onExpChangeEvent(final PlayerExpChangeEvent event) {
        final Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(BetonQuest.getInstance(), () -> {
            final double newAmount = player.getLevel() + player.getExp();
            qeHandler.handle(() -> onExperienceChange(profileProvider.getProfile(player), newAmount, false));
        });
    }

    /**
     * Checks the players experience when they join the server.
     *
     * @param event the event that triggered this method
     */
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final OnlineProfile onlineProfile = profileProvider.getProfile(player);
        final double newAmount = player.getLevel() + player.getExp();
        qeHandler.handle(() -> onExperienceChange(onlineProfile, newAmount, false));
    }
}
