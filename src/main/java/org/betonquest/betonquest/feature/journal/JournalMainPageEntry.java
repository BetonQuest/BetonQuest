package org.betonquest.betonquest.feature.journal;

import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.message.ParsedSectionMessage;

import java.util.List;

/**
 * A journal main page entry.
 *
 * @param priority   the order priority
 * @param conditions the conditions to display the entry
 * @param entry      the message content
 */
public record JournalMainPageEntry(int priority, List<ConditionID> conditions, ParsedSectionMessage entry) {
}
