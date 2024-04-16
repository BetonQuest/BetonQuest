package org.betonquest.betonquest.quest.event.conversation;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

/**
 * Factory for {@link ConversationEvent}.
 */
public class ConversationEventFactory implements EventFactory {
    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Server to use for syncing to the primary server thread.
     */
    private final Server server;

    /**
     * Scheduler to use for syncing to the primary server thread.
     */
    private final BukkitScheduler scheduler;

    /**
     * Plugin to use for syncing to the primary server thread.
     */
    private final Plugin plugin;

    /**
     * Create the conversation event factory.
     *
     * @param loggerFactory logger factory to use
     * @param server        server to use
     * @param scheduler     scheduler to use
     * @param plugin        plugin to use
     */
    public ConversationEventFactory(final BetonQuestLoggerFactory loggerFactory, final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.loggerFactory = loggerFactory;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final ConversationID conversationID;
        try {
            conversationID = new ConversationID(instruction.getPackage(), instruction.next());
        } catch (final ObjectNotFoundException e) {
            throw new InstructionParseException(e.getMessage(), e);
        }
        final String startingOption = getStartOption(instruction, conversationID);
        return new PrimaryServerThreadEvent(
                new OnlineProfileRequiredEvent(
                        loggerFactory.create(ConversationEventFactory.class), new ConversationEvent(loggerFactory, conversationID, startingOption), instruction.getPackage()
                ), server, scheduler, plugin
        );
    }

    /**
     * Gets an optional start option for the conversation.
     *
     * @param instruction    to get option name from
     * @param conversationID to get option from
     * @return null if no option argument is given, otherwise the option name
     * @throws InstructionParseException if no NPC option with the given name is present
     */
    @Nullable
    private String getStartOption(final Instruction instruction, final ConversationID conversationID) throws InstructionParseException {
        final String targetOptionName = instruction.getOptional("option");
        if (targetOptionName == null) {
            return null;
        }

        // We need to manually check the existence of the starting option because the conversation is not loaded yet.
        final String optionPath = "conversations." + conversationID.getBaseID() + ".NPC_options." + targetOptionName;
        if (!conversationID.getPackage().getConfig().contains(optionPath)) {
            throw new InstructionParseException("NPC Option '" + targetOptionName + "' does not exist in '" + conversationID + "'.");
        }

        return targetOptionName;
    }

}
