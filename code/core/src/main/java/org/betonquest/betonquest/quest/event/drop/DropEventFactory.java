package org.betonquest.betonquest.quest.event.drop;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;
import org.betonquest.betonquest.quest.event.OnlineProfileGroupPlayerlessEventAdapter;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;

/**
 * Factory to create {@link DropEvent}s for items from {@link Instruction}s.
 */
public class DropEventFactory implements PlayerActionFactory, PlayerlessActionFactory {

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
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createDropEvent(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createStaticDropEvent(instruction);
    }

    private PlayerlessAction createStaticDropEvent(final Instruction instruction) throws QuestException {
        final NullableActionAdapter dropEvent = createDropEvent(instruction);
        final boolean location = !instruction.bool().getFlag("location", true).getValue(null).orElse(false);
        if (location) {
            return new OnlineProfileGroupPlayerlessEventAdapter(profileProvider::getOnlineProfiles, dropEvent);
        }
        return dropEvent;
    }

    private NullableActionAdapter createDropEvent(final Instruction instruction) throws QuestException {
        final Argument<List<ItemWrapper>> items = instruction.item().list().notEmpty().get("items", Collections.emptyList());
        final String locationPart = instruction.string().get("location", "%location%").getValue(null);
        final Argument<Location> location = instruction.chainForArgument(locationPart).location().get();
        return new NullableActionAdapter(new DropEvent(items, location));
    }
}
