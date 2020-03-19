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
package pl.betoncraft.betonquest.id;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;

/**
 * Resolves a package and a corresponding path
 */
public abstract class ID {

    /**
     * String used as "up the hierarchy" package
     */
    public static final String UP_STR = "_";
    /**
     * String used as package separator
     */
    public static final String PACK_STR = "-";
    /**
     * String user as package id boarder
     */
    public static final String SEPARATOR_STR = ">";

    /**
     * The resolved package
     */
    private final ConfigPackage pack;
    /**
     * The resolved id
     */
    private String id;
    /**
     * Raw instruction generated from sub class
     */
    private final String rawInstruction;
    /**
     * Instruction generated from the rawInstruction
     */
    private Instruction instruction;

    /**
     * Generate an ID from the given {@link ConfigPackage} and the id.
     * 
     * @param pack
     *            The {@link ConfigPackage} from where the call takes place. If
     *            null, the default {@link ConfigPackage} will be used.
     * @param fullId
     *            The ID that should be accessed. The ID could contain an
     *            absolute or relative path.
     * @throws ObjectNotFoundException
     *             If there was an exception while matching the ID to a path.
     */
    public ID(final ConfigPackage pack, final String fullId) throws ObjectNotFoundException {
        if (fullId == null || fullId.length() == 0) {
            throw new ObjectNotFoundException("ID is null!");
        }

        if (fullId.contains(SEPARATOR_STR)) {
            final String[] idParts = fullId.split(SEPARATOR_STR);
            final String packName = idParts[0];
            final String idName = idParts[1];
            this.pack = packName.startsWith(UP_STR + PACK_STR) ? resolveRelative(pack, packName)
                    : Config.getPackages().get(packName);
            if (idName.length() == 0) {
                throw new ObjectNotFoundException("ID of the pack '" + this.pack + "' is null!");
            }
            this.id = idName;
        } else {
            this.pack = pack == null ? Config.getDefaultPackage() : pack;
            this.id = fullId;
        }

        if (this.pack == null) {
            throw new ObjectNotFoundException("Package in ID '" + fullId + "' does not exist!");
        }
        rawInstruction = generateRawInstruction();
    }

    private ConfigPackage resolveRelative(final ConfigPackage pack, final String relative)
            throws ObjectNotFoundException {
        if (pack == null) {
            throw new ObjectNotFoundException("Package for relative Path '" + relative + "'is null!");
        }
        final String[] packPath = pack.getName().split(PACK_STR);
        final String[] relativePath = relative.split(PACK_STR);

        final int stepsUp = countStepsUp(packPath, relativePath);
        final String absolutPath = buildAbsolutePath(packPath, relativePath, stepsUp);

        final ConfigPackage absolutPack = Config.getPackages().get(absolutPath);
        if (absolutPack == null) {
            throw new ObjectNotFoundException("Relative path in ID '" + relative + "' resolved to '" + absolutPath +
                    "', but this package does not exist!");
        }
        return absolutPack;
    }

    private int countStepsUp(final String[] packPath, final String[] relativePath) throws ObjectNotFoundException {
        int stepsUp = 0;
        while (stepsUp < relativePath.length && relativePath[stepsUp].equals(UP_STR)) {
            stepsUp++;
        }
        if (stepsUp > packPath.length) {
            throw new ObjectNotFoundException(
                    "Relative path goes out of package scope! Consider removing a few '"
                            + UP_STR + "'s in ID '" + String.join(PACK_STR, relativePath) + "'.");
        }
        return stepsUp;
    }

    private String buildAbsolutePath(final String[] packPath, final String[] relativePath, final int stepsUp) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < packPath.length - stepsUp; i++) {
            sb.append(packPath[i]).append(PACK_STR);
        }
        for (int i = stepsUp; i < relativePath.length; i++) {
            sb.append(relativePath[i]).append(PACK_STR);
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * @return The resolved package
     */
    public ConfigPackage getPackage() {
        return pack;
    }

    /**
     * @return The resolved ID
     */
    public String getBaseID() {
        return id;
    }

    /**
     * @return The full ID that contains pack and ID
     */
    public String getFullID() {
        return pack.getName() + SEPARATOR_STR + getBaseID();
    }

    @Override
    public String toString() {
        return getFullID();
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof ID) {
            final ID id = (ID) o;
            return id.id.equals(this.id) &&
                    id.pack.equals(this.pack);
        }
        return false;
    }

    /**
     * @return The instruction that is generated by the rawInstruction
     */
    public Instruction getInstruction() {
        if (rawInstruction == null) {
            return null;
        }
        if (instruction == null) {
            instruction = new Instruction(pack, this, rawInstruction);
        }
        return instruction;
    }

    /**
     * @return The rawInstruction generated by a sub class
     */
    protected abstract String generateRawInstruction() throws ObjectNotFoundException;
}