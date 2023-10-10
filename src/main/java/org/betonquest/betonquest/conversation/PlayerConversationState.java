package org.betonquest.betonquest.conversation;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConversationID;
import org.bukkit.Location;

/**
 * Represents the state of a conversation for a player.
 * The player may disconnect during a conversation that cannot be stopped.
 * In such a case, the conversation state is stored in this class and then resumed by the {@link ConversationResumer}.
 *
 * @param currentConversation the conversation that was active when the player disconnected
 * @param currentOption       the option that was selected by the player when the conversation was stopped
 * @param location            the location of the player when the conversation was started
 */

public record PlayerConversationState(ConversationID currentConversation, String currentOption, Location location) {

    /**
     * Creates a conversation state from a string.
     *
     * @param string the string representation of {@link PlayerConversationState}
     * @return the conversation state represented by the string
     * @throws ObjectNotFoundException if the conversation ID is invalid
     */
    public static PlayerConversationState fromString(final String string) throws ObjectNotFoundException {
        final String[] mainParts = string.split(" ");

        final String fullID = mainParts[0];
        final String[] splitID = fullID.split("\\.");
        final String packName = splitID[0];
        final String identifier = splitID[1];
        final QuestPackage questPackage = Config.getPackages().get(packName);

        final String optionName = mainParts[2];
        final String[] location = mainParts[3].split(";");

        return new PlayerConversationState(
                new ConversationID(questPackage, identifier),
                optionName,
                new Location(
                        org.bukkit.Bukkit.getWorld(location[3]),
                        Double.parseDouble(location[0]),
                        Double.parseDouble(location[1]),
                        Double.parseDouble(location[2])
                )
        );
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
        return currentConversation + " " + currentOption + " " +
                "%s;%s;%s;%s".formatted(location.getX(), location.getY(), location.getZ(), location.getWorld().getName());

    }
}

