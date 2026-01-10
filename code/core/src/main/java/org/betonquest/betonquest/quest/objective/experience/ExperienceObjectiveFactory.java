package org.betonquest.betonquest.quest.objective.experience;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.event.PlayerObjectiveChangeEvent;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.event.ObjectiveService;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.quest.action.IngameNotificationSender;
import org.betonquest.betonquest.quest.action.NotificationLevel;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;

/**
 * Factory for creating {@link ExperienceObjective} instances from {@link Instruction}s.
 */
public class ExperienceObjectiveFactory implements ObjectiveFactory {

    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Creates a new instance of the ExperienceObjectiveFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public ExperienceObjectiveFactory(final BetonQuestLoggerFactory loggerFactory, final PluginMessage pluginMessage) {
        this.loggerFactory = loggerFactory;
        this.pluginMessage = pluginMessage;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<Number> amount = instruction.number().get();
        final BetonQuestLogger log = loggerFactory.create(ExperienceObjective.class);
        final IngameNotificationSender levelSender = new IngameNotificationSender(log,
                pluginMessage, instruction.getPackage(), instruction.getID().getFull(),
                NotificationLevel.INFO, "level_to_gain");
        final ExperienceObjective objective = new ExperienceObjective(service, amount, levelSender);
        service.request(PlayerLevelChangeEvent.class).priority(EventPriority.MONITOR).onlineHandler(objective::onLevelChangeEvent)
                .player(PlayerLevelChangeEvent::getPlayer).subscribe(true);
        service.request(PlayerExpChangeEvent.class).priority(EventPriority.MONITOR).onlineHandler(objective::onExpChangeEvent)
                .player(PlayerExpChangeEvent::getPlayer).subscribe(true);
        service.request(PlayerJoinEvent.class).onlineHandler(objective::onPlayerJoin)
                .player(PlayerJoinEvent::getPlayer).subscribe(false);
        service.request(PlayerObjectiveChangeEvent.class).handler(objective::onStart)
                .profile(PlayerObjectiveChangeEvent::getProfile).subscribe(false);
        return objective;
    }
}
