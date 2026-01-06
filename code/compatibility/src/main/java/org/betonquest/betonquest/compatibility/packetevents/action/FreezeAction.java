package org.betonquest.betonquest.compatibility.packetevents.action;

import com.github.retrooper.packetevents.PacketEventsAPI;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;
import org.betonquest.betonquest.compatibility.packetevents.passenger.FakeArmorStandPassenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Event to block all player entity movement.
 */
public class FreezeAction implements OnlineAction {

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The PacketEvents API instance.
     */
    private final PacketEventsAPI<?> packetEventsAPI;

    /**
     * Freeze duration.
     */
    private final Argument<Number> ticks;

    /**
     * Create a new event that freezes a player.
     *
     * @param plugin          the plugin instance
     * @param packetEventsAPI the PacketEvents API instance
     * @param ticks           the freeze duration
     */
    public FreezeAction(final Plugin plugin, final PacketEventsAPI<?> packetEventsAPI, final Argument<Number> ticks) {
        this.plugin = plugin;
        this.packetEventsAPI = packetEventsAPI;
        this.ticks = ticks;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final int ticks = this.ticks.getValue(profile).intValue();
        final Player player = profile.getPlayer();

        final FakeArmorStandPassenger armorStandPassenger = new FakeArmorStandPassenger(plugin, packetEventsAPI, player);
        armorStandPassenger.mount(player.getLocation());

        Bukkit.getScheduler().runTaskLater(plugin, armorStandPassenger::unmount, ticks);
    }
}
