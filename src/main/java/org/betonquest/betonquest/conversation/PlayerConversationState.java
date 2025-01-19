package org.betonquest.betonquest.conversation;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exception.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConversationID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents the state of a conversation for a player.
 * The player may disconnect during a conversation that cannot be stopped.
 * In such a case, the conversation state is stored in this class and then resumed by the {@link ConversationResumer}.
 *
 * @param currentConversation the conversation that was active when the player disconnected
 * @param currentOption       the option that was selected by the player when the conversation was stopped
 * @param center              the center of the conversation
 */

public record PlayerConversationState(ConversationID currentConversation, String currentOption, Location center) {

    /**
     * The required amount of arguments in the string representation of the conversation state.
     * The string must be split at every space to obtain the arguments.
     */
    private static final int REQUIRED_AMOUNT_OF_ARGUMENTS = 3;

    /**
     * Creates a conversation state from a string.
     *
     * @param string the string representation of {@link PlayerConversationState}
     * @return the conversation state represented by the string
     * @throws ObjectNotFoundException if the conversation ID is invalid
     */
    public static Optional<PlayerConversationState> fromString(@Nullable final String string) throws ObjectNotFoundException {
        if (string == null || string.isEmpty()) {
            return Optional.empty();
        }

        final String[] mainParts = string.split(" ");

        if (mainParts.length != REQUIRED_AMOUNT_OF_ARGUMENTS) {
            return Optional.empty();
        }
        final String fullID = mainParts[0];
        final String[] splitID = fullID.split("\\.");
        final String packName = splitID[0];
        final QuestPackage questPackage = Config.getPackages().get(packName);
        if (questPackage == null) {
            throw new ObjectNotFoundException("The package " + packName + " does not exist!");
        }
        final String identifier = splitID[1];
        final ConversationID currentConversation = new ConversationID(questPackage, identifier);

        final String optionName = mainParts[1];

        final String[] locationString = mainParts[2].split(";");
        final Location location = new Location(Bukkit.getWorld(locationString[3]), Double.parseDouble(locationString[0]), Double.parseDouble(locationString[1]), Double.parseDouble(locationString[2]));

        return Optional.of(new PlayerConversationState(currentConversation, optionName, location));
    }

    /**
     * Transforms the conversation state into a string in which the conversation's ID,
     * the current option and the location are split by a space. The location's coordinates and world are seperated by
     * semicolons.
     *
     * @return the string representation of the conversation state
     */
    @Override
    public String toString() {
        return currentConversation + " " + currentOption + " " + "%s;%s;%s;%s".formatted(center.getX(), center.getY(), center.getZ(), center.getWorld().getName());
    }
}
