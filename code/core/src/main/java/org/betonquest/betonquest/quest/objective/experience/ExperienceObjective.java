package org.betonquest.betonquest.quest.objective.experience;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.PlayerObjectiveChangeEvent;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.ObjectiveState;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveFactoryService;
import org.betonquest.betonquest.quest.action.IngameNotificationSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;

import java.util.Locale;

/**
 * Player needs to get specified experience level or more.
 */
public class ExperienceObjective extends DefaultObjective {

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
     * @param service     the objective factory service
     * @param amount      the experience level the player needs to get
     * @param levelSender the notification to send when the player gains experience
     * @throws QuestException if there is an error in the instruction
     */
    public ExperienceObjective(final ObjectiveFactoryService service, final Argument<Number> amount, final IngameNotificationSender levelSender) throws QuestException {
        super(service);
        this.amount = amount;
        this.levelSender = levelSender;
    }

    private void onExperienceChange(final OnlineProfile onlineProfile, final double newAmount, final boolean notify) throws QuestException {
        final double amount = this.amount.getValue(onlineProfile).doubleValue();
        if (newAmount >= amount) {
            completeObjective(onlineProfile);
        } else if (this.hasNotify(onlineProfile) && notify) {
            final int level = (int) (amount - newAmount);
            if (level % getNotifyInterval(onlineProfile) == 0) {
                levelSender.sendNotification(onlineProfile, new VariableReplacement("amount", Component.text(level)));
            }
        }
    }

    /**
     * Called when the player starts the objective.
     *
     * @param event   the event
     * @param profile the profile
     */
    public void onStart(final PlayerObjectiveChangeEvent event, final Profile profile) {
        if (event.getState() != ObjectiveState.ACTIVE) {
            return;
        }
        profile.getOnlineProfile()
                .ifPresent(onlineProfile -> {
                    final Player player = onlineProfile.getPlayer();
                    final double newAmount = player.getLevel() + player.getExp();
                    getExceptionHandler().handle(() -> onExperienceChange(onlineProfile, newAmount, false));
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
     * @param event         the event that triggered this method
     * @param onlineProfile the profile of the player that leveled up
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onLevelChangeEvent(final PlayerLevelChangeEvent event, final OnlineProfile onlineProfile) throws QuestException {
        final Player player = event.getPlayer();
        final double newAmount = player.getLevel() + player.getExp();
        onExperienceChange(onlineProfile, newAmount, true);
    }

    /**
     * Checks the players experience when they gain experience.
     *
     * @param event         the event that triggered this method
     * @param onlineProfile the profile of the player that gained experience
     */
    public void onExpChangeEvent(final PlayerExpChangeEvent event, final OnlineProfile onlineProfile) {
        final Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(BetonQuest.getInstance(), () -> {
            final double newAmount = player.getLevel() + player.getExp();
            getExceptionHandler().handle(() -> onExperienceChange(onlineProfile, newAmount, false));
        });
    }

    /**
     * Checks the players experience when they join the server.
     *
     * @param event         the event that triggered this method
     * @param onlineProfile the profile of the player that joined
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onPlayerJoin(final PlayerJoinEvent event, final OnlineProfile onlineProfile) throws QuestException {
        final Player player = event.getPlayer();
        final double newAmount = player.getLevel() + player.getExp();
        onExperienceChange(onlineProfile, newAmount, false);
    }
}
