package org.betonquest.betonquest.compatibility.holograms.decentholograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class DecentHologramsHologram implements BetonHologram {
    private final Hologram hologram;

    public DecentHologramsHologram(String name, final Location location) {
        //This API may need to be updated in the near future..
        if (DHAPI.getHologram(name) != null) {
            //In the rare case that a hologram is created with a name that already exists...
            name = UUID.randomUUID().toString();
        }
        hologram = DHAPI.createHologram(name, location);
        hologram.enable();
    }

    @Override
    public void appendLine(final ItemStack item) {
        DHAPI.addHologramLine(hologram, item);
    }

    @Override
    public void appendLine(final String text) {
        DHAPI.addHologramLine(hologram, text);
    }

    @Override
    public void setLine(final int index, final ItemStack item) {
        DHAPI.setHologramLine(hologram, index, item);
    }

    @Override
    public void setLine(final int index, final String text) {
        DHAPI.setHologramLine(hologram, index, text);
    }

    @Override
    public void insertLine(final int index, final ItemStack item) {
        DHAPI.insertHologramLine(hologram, index, item);
    }

    @Override
    public void insertLine(final int index, final String text) {
        DHAPI.insertHologramLine(hologram, index, text);
    }

    @Override
    public void removeLine(final int index) {
        DHAPI.removeHologramLine(hologram, index);
    }

    @Override
    public void show(final Player player) {
        hologram.show(player, 0);
    }

    @Override
    public void hide(final Player player) {
        hologram.hide(player);
    }

    @Override
    public void move(final Location location) {
        DHAPI.moveHologram(hologram, location);
    }

    @Override
    public void showAll() {
        hologram.showAll();
    }

    @Override
    public void hideAll() {
        hologram.hideAll();
    }

    @Override
    public void delete() {
        hologram.destroy();
        hologram.delete();
    }
}
