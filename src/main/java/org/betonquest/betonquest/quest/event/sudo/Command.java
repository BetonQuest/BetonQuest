package org.betonquest.betonquest.quest.event.sudo;

import java.util.List;

/**
 * Represents a command and the variables to be run.
 */
public record Command(String command, List<String> variables) {
}
