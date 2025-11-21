package org.betonquest.betonquest.feature.journal;

import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.text.Text;

import java.util.List;

/**
 * A journal main page entry.
 *
 * @param priority   the order priority
 * @param conditions the conditions to display the entry
 * @param entry      the text content
 */
public record JournalMainPageEntry(int priority, Variable<List<ConditionID>> conditions, Text entry) {
}
