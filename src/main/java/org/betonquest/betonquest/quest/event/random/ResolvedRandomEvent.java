package org.betonquest.betonquest.quest.event.random;


import org.betonquest.betonquest.id.EventID;

/**
 * Represents an event with its chance.
 *
 * @param eventID the event to be executed
 * @param chance  the resolved chance of the event
 */
record ResolvedRandomEvent(EventID eventID, double chance) {
}
