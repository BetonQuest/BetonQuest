package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HolographicDisplaysHologram implements BetonHologram {
    private final Hologram hologram;

    public HolographicDisplaysHologram(final String name, final Location location) {
        hologram = HologramsAPI.createHologram(BetonQuest.getInstance(), location);
    }

    @Override
    public void appendLine(final ItemStack item) {
        hologram.appendItemLine(item);
    }

    @Override
    public void appendLine(final String text) {
        hologram.appendTextLine(text);
    }

    @Override
    public void setLine(final int index, final ItemStack item) {
        hologram.removeLine(index);
        hologram.insertItemLine(index, item);
    }

    @Override
    public void setLine(final int index, final String text) {
        hologram.removeLine(index);
        hologram.insertTextLine(index, text);
    }

    @Override
    public void insertLine(final int index, final ItemStack item) {
        hologram.insertItemLine(index, item);
    }

    @Override
    public void insertLine(final int index, final String text) {
        hologram.insertTextLine(index, text);
    }

    @Override
    public void removeLine(final int index) {
        hologram.removeLine(index);
    }

    @Override
    public void show(final Player player) {
        hologram.getVisibilityManager().showTo(player);
    }

    @Override
    public void hide(final Player player) {
        hologram.getVisibilityManager().hideTo(player);
    }

    @Override
    public void move(final Location location) {
        hologram.teleport(location);
    }

    @Override
    public void showAll() {
        final VisibilityManager manager = hologram.getVisibilityManager();
        manager.setVisibleByDefault(true);
        manager.resetVisibilityAll();
    }

    @Override
    public void hideAll() {
        final VisibilityManager manager = hologram.getVisibilityManager();
        manager.setVisibleByDefault(false);
        manager.resetVisibilityAll();
    }

    @Override
    public void delete() {
        hologram.delete();
    }
}
