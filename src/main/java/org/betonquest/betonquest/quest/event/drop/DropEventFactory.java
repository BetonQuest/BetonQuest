package org.betonquest.betonquest.quest.event.drop;

import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.OnlineProfileGroupPlayerlessEventAdapter;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadPlayerlessEvent;
import org.bukkit.Location;

/**
 * Factory to create {@link DropEvent}s for items from {@link Instruction}s.
 */
public class DropEventFactory implements PlayerEventFactory, PlayerlessEventFactory {
    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Creates the drop event factory.
     *
     * @param profileProvider the profile provider instance
     * @param data            the data for primary server thread access
     */
    public DropEventFactory(final ProfileProvider profileProvider, final PrimaryServerThreadData data) {
        this.profileProvider = profileProvider;
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(createDropEvent(instruction), data);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessEvent(createStaticDropEvent(instruction), data);
    }

    private PlayerlessEvent createStaticDropEvent(final Instruction instruction) throws QuestException {
        final NullableEventAdapter dropEvent = createDropEvent(instruction);
        if (!instruction.hasArgument("location")) {
            return new OnlineProfileGroupPlayerlessEventAdapter(profileProvider::getOnlineProfiles, dropEvent);
        }
        return dropEvent;
    }

    private NullableEventAdapter createDropEvent(final Instruction instruction) throws QuestException {
        final VariableList<Item> items = parseItemList(instruction);
        final Variable<Location> location = instruction.getVariable(instruction.getOptional("location", "%location%"), Argument.LOCATION);
        return new NullableEventAdapter(new DropEvent(items, location));
    }

    private VariableList<Item> parseItemList(final Instruction instruction) throws QuestException {
        return instruction.get(instruction.getOptional("items", ""), PackageArgument.ofList(PackageArgument.ITEM, VariableList.notEmptyChecker()));
    }
}
