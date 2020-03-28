package pl.betoncraft.betonquest.multiversion.item;

import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This class load an ItemStack in (main)hand, that depending on the minecraft
 * version
 */
public final class ItemInHandHandler {

    private ItemInHandHandler() {}

    /**
     * Get the version depending item in (main)hand
     * 
     * @param event
     *            The PlayerInteractEntityEvent
     * @return The ItemStack
     */
    public static ItemStack getItemInHand(final PlayerInteractEntityEvent event) {
        return event.getPlayer().getItemInHand();
    }
}
