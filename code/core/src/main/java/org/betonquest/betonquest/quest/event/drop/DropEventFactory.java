package org.betonquest.betonquest.quest.event.drop;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.quest.event.OnlineProfileGroupPlayerlessEventAdapter;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;

/**
 * Factory to create {@link DropEvent}s for items from {@link Instruction}s.
 */
public class DropEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Creates the drop event factory.
     *
     * @param profileProvider the profile provider instance
     */
    public DropEventFactory(final ProfileProvider profileProvider) {
        this.profileProvider = profileProvider;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createDropEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return createStaticDropEvent(instruction);
    }

    private PlayerlessEvent createStaticDropEvent(final Instruction instruction) throws QuestException {
        final NullableEventAdapter dropEvent = createDropEvent(instruction);
        final boolean location = !instruction.bool().getFlag("location", false).getValue(null).orElse(false);
        if (location) {
            return new OnlineProfileGroupPlayerlessEventAdapter(profileProvider::getOnlineProfiles, dropEvent);
        }
        return dropEvent;
    }

    private NullableEventAdapter createDropEvent(final Instruction instruction) throws QuestException {
        final Argument<List<ItemWrapper>> items = instruction.item().list().notEmpty().get("items", Collections.emptyList());
        final String locationPart = instruction.string().get("location", "%location%").getValue(null);
        final Argument<Location> location = instruction.chainForArgument(locationPart).location().get();
        return new NullableEventAdapter(new DropEvent(items, location));
    }
}
