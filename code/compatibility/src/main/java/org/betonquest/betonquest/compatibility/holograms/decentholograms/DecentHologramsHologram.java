package org.betonquest.betonquest.compatibility.holograms.decentholograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * DecentHolograms specific implementation of BetonHologram.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class DecentHologramsHologram implements BetonHologram {

    /**
     * The hologram object from DecentHolograms.
     */
    private final Hologram hologram;

    /**
     * Create a BetonHologram to wrap the given DecentHolograms hologram.
     *
     * @param hologram The hologram object to wrap
     */
    public DecentHologramsHologram(final Hologram hologram) {
        this.hologram = hologram;
    }

    @Override
    public void appendLine(final ItemStack item) {
        DHAPI.addHologramLine(hologram, item);
    }

    @Override
    public void appendLine(final Component text) {
        DHAPI.addHologramLine(hologram, translate(text));
    }

    @Override
    public void setLine(final int index, final ItemStack item) {
        DHAPI.setHologramLine(hologram, index, item);
    }

    @Override
    public void setLine(final int index, final Component text) {
        DHAPI.setHologramLine(hologram, index, translate(text));
    }

    private String translate(final Component text) {
        return LegacyComponentSerializer.legacySection().serialize(text);
    }

    @Override
    public void createLines(final int startingIndex, final int linesAdded) {
        final HologramPage page = DHAPI.getHologramPage(hologram, 0);
        if (page == null) {
            throw new IllegalStateException("Hologram page at index 0 should not null!");
        }
        for (int i = startingIndex; i < linesAdded; i++) {
            if (i >= page.size()) {
                DHAPI.addHologramLine(hologram, "");
            }
        }
    }

    @Override
    public void removeLine(final int index) {
        DHAPI.removeHologramLine(hologram, index);
    }

    @Override
    public void show(final Player player) {
        if (!hologram.isVisible(player)) {
            hologram.removeHidePlayer(player);
            hologram.setShowPlayer(player);
            hologram.show(player, 0);
        }
    }

    @Override
    public void hide(final Player player) {
        if (hologram.isVisible(player)) {
            hologram.removeShowPlayer(player);
            hologram.setHidePlayer(player);
            hologram.hide(player);
        }
    }

    @Override
    public void move(final Location location) {
        DHAPI.moveHologram(hologram, location);
    }

    @Override
    public void showAll() {
        final List<Player> players = hologram.getViewerPlayers();
        players.forEach(hologram::removeHidePlayer);
        players.forEach(hologram::setShowPlayer);
        hologram.setDefaultVisibleState(true);
        hologram.showAll();
    }

    @Override
    public void hideAll() {
        final List<Player> players = hologram.getViewerPlayers();
        players.forEach(hologram::removeShowPlayer);
        players.forEach(hologram::setHidePlayer);
        hologram.setDefaultVisibleState(false);
        hologram.hideAll();
    }

    @Override
    public void delete() {
        hologram.destroy();
        DHAPI.removeHologram(hologram.getName());
    }

    @Override
    public boolean isDisabled() {
        return hologram.isDisabled();
    }

    @Override
    public void disable() {
        hologram.disable();
    }

    @Override
    public void enable() {
        hologram.enable();
    }

    @Override
    public int size() {
        return hologram.getPage(0).size();
    }

    @Override
    public void clear() {
        final HologramPage page = hologram.getPage(0);
        for (int i = page.size() - 1; i >= 0; i--) {
            page.removeLine(i);
        }
    }

    @Override
    public Location getLocation() {
        return hologram.getLocation();
    }
}
