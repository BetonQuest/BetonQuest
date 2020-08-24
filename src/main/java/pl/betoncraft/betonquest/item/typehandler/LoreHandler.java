/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.item.typehandler;

import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem.Existence;

import java.util.LinkedList;
import java.util.List;

public class LoreHandler {

    private List<String> lore = new LinkedList<>();
    private Existence existence = Existence.WHATEVER;
    private boolean exact = true;

    public LoreHandler() {}

    public void set(final String lore) throws InstructionParseException {
        if (lore.equals("none")) {
            existence = Existence.FORBIDDEN;
            return;
        }
        existence = Existence.REQUIRED;
        for (final String line : lore.split(";")) {
            this.lore.add(line.replaceAll("_", " ").replaceAll("&", "§"));
        }
    }

    public void setNotExact() {
        exact = false;
    }

    public List<String> get() {
        return lore;
    }

    public boolean check(final List<String> lore) {
        switch (existence) {
            case WHATEVER:
                return true;
            case REQUIRED:
                if (lore == null) {
                    return false;
                }
                if (exact) {
                    if (this.lore.size() != lore.size()) {
                        return false;
                    }
                    for (int i = 0; i < lore.size(); i++) {
                        if (!this.lore.get(i).equals(lore.get(i))) {
                            return false;
                        }
                    }
                } else {
                    for (final String line : this.lore) {
                        boolean has = false;
                        for (final String itemLine : lore) {
                            if (itemLine.equals(line)) {
                                has = true;
                                break;
                            }
                        }
                        if (!has) {
                            return false;
                        }
                    }
                }
                return true;
            case FORBIDDEN:
                return lore == null || lore.isEmpty();
            default:
                return false;
        }
    }

}
