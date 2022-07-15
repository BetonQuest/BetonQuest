package org.betonquest.betonquest.quest.event.journal;

import org.betonquest.betonquest.Journal;

/**
 * A journal changer that will not change anything.
 */
public class NoActionJournalChanger implements JournalChanger {
	/**
	 * Create the no action journal changer.
	 */
	public NoActionJournalChanger() {}

	@Override
	public void changeJournal(final Journal journal) {
		// null object pattern
	}

}
