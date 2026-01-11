package org.betonquest.betonquest.feature.journal;

import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.text.Text;

import java.util.List;

/**
 * A journal main page entry.
 *
 * @param priority   the order priority
 * @param conditions the conditions to display the entry
 * @param entry      the text content
 */
public record JournalMainPageEntry(int priority, Argument<List<ConditionIdentifier>> conditions, Text entry) {

}
