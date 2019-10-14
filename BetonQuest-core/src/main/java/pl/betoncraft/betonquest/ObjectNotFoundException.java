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
package pl.betoncraft.betonquest;

/**
 * Exception thrown when the object couldn't be found.
 * @deprecated Use the {@link pl.betoncraft.betonquest.exceptions.ObjectNotFoundException} instead, this will be removed in the near future
 */
public class ObjectNotFoundException extends pl.betoncraft.betonquest.exceptions.ObjectNotFoundException {

    private static final long serialVersionUID = -6335789753445719198L;
    
    /**
     * {@link Exception#Exception(String)}
     */
    public ObjectNotFoundException(final String message) {
        super(message);
    }

    /**
     * {@link Exception#Exception(String, Throwable)}
     */
    public ObjectNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * {@link Exception#Exception(Throwable)}
     */
    public ObjectNotFoundException(final Throwable cause) {
        super(cause);
    }
}
