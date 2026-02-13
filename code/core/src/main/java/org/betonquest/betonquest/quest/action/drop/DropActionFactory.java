package org.betonquest.betonquest.quest.action.drop;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.quest.action.OnlineProfileGroupPlayerlessActionAdapter;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;

/**
 * Factory to create {@link DropAction}s for items from {@link Instruction}s.
 */
public class DropActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Creates the drop action factory.
     *
     * @param profileProvider the profile provider instance
     */
    public DropActionFactory(final ProfileProvider profileProvider) {
        this.profileProvider = profileProvider;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createDropAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createStaticDropAction(instruction);
    }

    private PlayerlessAction createStaticDropAction(final Instruction instruction) throws QuestException {
        final NullableActionAdapter dropAction = createDropAction(instruction);
        if (instruction.location().get("location").isEmpty()) {
            return new OnlineProfileGroupPlayerlessActionAdapter(profileProvider::getOnlineProfiles, dropAction);
        }
        return dropAction;
    }

    private NullableActionAdapter createDropAction(final Instruction instruction) throws QuestException {
        final Argument<List<ItemWrapper>> items = instruction.item().list().notEmpty().get("items", Collections.emptyList());
        final Argument<Location> location = instruction.location().get("location").orElse(
                profile -> profile.getOnlineProfile()
                        .orElseThrow(() -> new QuestException("Can't get location of offline player"))
                        .getPlayer().getLocation());
        return new NullableActionAdapter(new DropAction(items, location));
    }
}
