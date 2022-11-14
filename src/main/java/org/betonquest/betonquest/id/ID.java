package org.betonquest.betonquest.id;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;

import java.util.List;
import java.util.Objects;

@SuppressWarnings({"PMD.ShortClassName", "PMD.AbstractClassWithoutAbstractMethod", "PMD.CommentRequired"})
@CustomLog
public abstract class ID {

    public static final String UP_STR = "_"; // string used as "up the hierarchy" package

    public static final List<String> PATHS = List.of("events", "conditions", "objectives", "variables");

    protected String identifier;
    protected QuestPackage pack;
    protected Instruction instruction;
    protected String rawInstruction;

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity"})
    protected ID(final QuestPackage pack, final String identifier) throws ObjectNotFoundException {
        // id must be specified
        if (identifier == null || identifier.length() == 0) {
            throw new ObjectNotFoundException("ID is null");
        }
        // resolve package name
        if (identifier.contains(".")) {
            // id has specified a package, get it!
            int dotIndex = identifier.indexOf('.');
            final String packName = identifier.substring(0, dotIndex);
            if (pack != null && packName.startsWith(UP_STR + "-")) {
                // resolve relative name if we have a supplied package
                final String[] root = pack.getPackagePath().split("-");
                final String[] path = packName.split("-");
                // count how many packages up we need to go
                int stepsUp = 0;
                while (stepsUp < path.length && UP_STR.equals(path[stepsUp])) {
                    stepsUp++;
                }
                // can't go out of BetonQuest folder of course
                if (stepsUp > root.length) {
                    throw new ObjectNotFoundException("Relative path goes out of package scope! Consider removing a few '"
                            + UP_STR + "'s in ID " + identifier);
                }
                // construct the final absolute path
                final StringBuilder builder = new StringBuilder();
                for (int i = 0; i < root.length - stepsUp; i++) {
                    builder.append(root[i]).append('-');
                }
                for (int i = stepsUp; i < path.length; i++) {
                    builder.append(path[i]).append('-');
                }
                final String absolute = builder.substring(0, builder.length() - 1);
                this.pack = Config.getPackages().get(absolute);
                // throw error earlier so it can have more information than default one at the bottom
                if (this.pack == null) {
                    throw new ObjectNotFoundException("Relative path in ID '" + identifier + "' resolved to '" + absolute +
                            "', but this package does not exist!");
                }
                // We want to go down
            } else if (pack != null && packName.startsWith("-")) {
                final String currentPath = pack.getPackagePath();
                final String fullPath = currentPath + packName;

                this.pack = Config.getPackages().get(fullPath);
                // throw error earlier so it can have more information than default one at the bottom
                if (this.pack == null) {
                    throw new ObjectNotFoundException("Relative path in ID '" + identifier + "' resolved to '" + fullPath +
                            "', but this package does not exist!");
                }
            } else {
                // if no relative path is available, check if packName is a package or if it is an ID
                final String[] parts = identifier.split("\\.");


                ////////////////////////////////////////////

                if (parts.length == 1) {
                    // could be an event, condition or objective belonging to param 'pack'
                    this.pack = pack;
                    dotIndex = -1;
                } else if (parts.length == 2) {
                    // could be (package with event, condition or objective) OR a (non-package related variable)
                    final QuestPackage potentialPack = Config.getPackages().get(packName);
                    if (BetonQuest.isVariableType(packName)) {
                        //If only a length of 2 could player.event1 or player.display
                        //If packName is a variable type but if ALSO a package type
                        if (potentialPack == null) {
                            //this is true for player.display if player is not a package
                            this.pack = pack;
                            dotIndex = -1;
                        } else {
                            // if packName shares name with variable type
                            // this is true for player.event1 or player.display if player is a package
                            if (isIdFromPack(potentialPack, parts[1])) {
                                // this is true for player.event1
                                this.pack = potentialPack;
                            } else {
                                // this is true for player.display (since display isn't a variable)
                                this.pack = pack;
                                dotIndex = -1;
                            }
                        }
                    } else {
                        this.pack = potentialPack;
                    }
                } else if (parts.length > 2) {
                    // could be a package-related variable with or without a package.
                    // could be point.testpoint.amount OR package.point.testpoint.amount OR point.point.testpoint.amount
                    // or even point.point.point.amount which means a point variable named 'point' in the package called 'point'
                    // or even point.point.amount
                    final QuestPackage potentialPack = Config.getPackages().get(packName);
                    if (potentialPack == null) {
                        this.pack = pack;
                        dotIndex = -1;
                    } else {
                        // first term is a package
                        if (BetonQuest.isVariableType(packName)) {
                            // if packName same as variable type
                            if (BetonQuest.isVariableType(parts[1]) && isIdFromPack(potentialPack, parts[2])) {
                                //for point.point.point.amount or point.point.test.amount
                                this.pack = potentialPack;
                            } else if (isIdFromPack(potentialPack, parts[1])) {
                                //for point.point.amount or point.test.amount
                                this.pack = pack;
                                dotIndex = -1;
                            } else {
                                //that point.globalpoint.test.amount
                                this.pack = potentialPack;
                            }
                        } else {
                            this.pack = potentialPack;
                        }

                        if (BetonQuest.isVariableType(parts[1])) {
                            // second term is a variable type, check if next term is a valid id. If so we can expect the
                            // form of package.variable.id.args
                            if (isIdFromPack(potentialPack, parts[2])) {
                                this.pack = potentialPack;
                            }

                        } else if (BetonQuest.isVariableType(parts[0]) && isIdFromPack(potentialPack, parts[1])) {
                            // first term is also a variable, check if next term is a valid id. If so we can expect the
                            // form of variable.id.args
                            this.pack = pack;
                            dotIndex = -1;
                        }
                    }
                }

                if (this.pack == null) {
                    //if packName was not a pack, use provided pack and treat the entire raw identifier as the full id.
                    this.pack = pack;
                    dotIndex = -1;
                }
            }
            if (identifier.length() == dotIndex + 1) {
                throw new ObjectNotFoundException("ID of the pack '" + this.pack + "' is null");
            }
            this.identifier = identifier.substring(dotIndex + 1);
        } else {
            if (pack == null) {
                throw new ObjectNotFoundException("No package specified for id '" + identifier + "'!");
            }
            this.pack = pack;
            this.identifier = identifier;
        }

        // no package yet? this is an error
        if (this.pack == null) {
            throw new ObjectNotFoundException("Package in ID '" + identifier + "' does not exist");
        }
    }

    /**
     * Checks if an ID belongs to a provided QuestPackage. This checks all events, conditions, objectives and variables
     * for any ID matching the provided string
     *
     * @param pack The quest package to search
     * @param id   The id
     * @return true if the id exists in the quest package
     */
    private boolean isIdFromPack(final QuestPackage pack, final String id) {
        final MultiConfiguration config = pack.getConfig();
        for (final String path : PATHS) {
            if (config.getString(path + "." + id, null) != null) {
                return true;
            }
        }
        return false;
    }

    public QuestPackage getPackage() {
        return pack;
    }

    public String getBaseID() {
        return identifier;
    }

    public String getFullID() {
        return pack.getPackagePath() + "." + getBaseID();
    }

    @Override
    public String toString() {
        return getFullID();
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof ID identifier) {
            return identifier.identifier.equals(this.identifier) &&
                    identifier.pack.getPackagePath().equals(this.pack.getPackagePath());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, pack.getPackagePath());
    }

    public Instruction generateInstruction() {
        if (rawInstruction == null) {
            return null;
        }
        if (instruction == null) {
            instruction = new Instruction(pack, this, rawInstruction);
        }
        return instruction;
    }

}
