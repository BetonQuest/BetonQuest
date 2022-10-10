package org.betonquest.betonquest.compatibility.holograms.decentholograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@SuppressWarnings({"PMD.CommentRequired", "PMD.TooManyMethods"})
public class DecentHologramsHologram implements BetonHologram {
    private final Hologram hologram;

    public DecentHologramsHologram(final String name, final Location location) {
        //This API may need to be updated in the near future..
        String hologramName = name;
        if (DHAPI.getHologram(hologramName) != null) {
            //In the rare case that a hologram is created with a name that already exists...
            hologramName = UUID.randomUUID().toString();
        }
        hologram = DHAPI.createHologram(hologramName, location);
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
        /*
        Since DecentHolograms supports multiple pages whilst HolographicDisplays does not, we always assume
        that when modifying/inserting lines that we are dealing with page 0
         */
        hologram.removeHidePlayer(player);
    }

    @Override
    public void hide(final Player player) {
        hologram.setHidePlayer(player);

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
        DHAPI.removeHologram(hologram.getName());
    }
}
