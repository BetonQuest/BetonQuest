package org.betonquest.betonquest.id;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.instruction.Instruction;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Identifies any object(events, objectives, conversations etc.) of BetonQuest's scripting system via the path syntax.
 * Handles relative and absolute paths.
 */
@SuppressWarnings({"PMD.ShortClassName", "PMD.AbstractClassWithoutAbstractMethod", "PMD.GodClass"})
public abstract class ID {

    /**
     * The string used to go "up the hierarchy" in relative paths.
     */
    public static final String UP_STR = "_";

    /**
     * A list of all objects that can be addressed via this ID.
     */
    public static final List<String> PATHS = List.of("events", "conditions", "objectives", "variables",
            "conversations", "cancel", "items", "npcs");

    /**
     * The package the object is in.
     */
    protected final QuestPackage pack;

    /**
     * The identifier of the object without the package name.
     */
    protected String identifier;

    /**
     * The created instruction of the object.
     */
    @Nullable
    protected Instruction instruction;

    /**
     * Creates a new ID. Handles relative and absolute paths and edge cases with special IDs like variables.
     *
     * @param pack       the package the ID is in
     * @param identifier the id instruction string
     * @throws QuestException if the ID could not be parsed
     */
    protected ID(@Nullable final QuestPackage pack, final String identifier) throws QuestException {
        if (identifier.isEmpty()) {
            throw new QuestException("ID is null");
        }
        if (identifier.contains(".")) {
            final int dotIndex = identifier.indexOf('.');
            final QuestPackage parsed = parsePackageFromIdentifier(pack, identifier, dotIndex);
            if (parsed != null) {
                this.pack = parsed;
                this.identifier = identifier.substring(dotIndex + 1);
                return;
            }
            if (pack == null) {
                throw new QuestException("Package in ID '" + identifier + "' does not exist");
            }
        }
        if (pack == null) {
            throw new QuestException("No package specified for id '" + identifier + "'!");
        }
        this.pack = pack;
        this.identifier = identifier;
    }

    /**
     * Constructor of an id that also create an instruction.
     *
     * @param pack       the package the ID is in
     * @param identifier the id instruction string
     * @param section    the section of the config file
     * @param readable   the readable name of the object
     * @throws QuestException if the instruction or ID could not be created.
     */
    protected ID(@Nullable final QuestPackage pack, final String identifier, final String section, final String readable) throws QuestException {
        this(pack, identifier);
        final String rawInstruction = this.pack.getString(section + "." + this.identifier);
        if (rawInstruction == null) {
            throw new QuestException(readable + " '" + getFullID() + "' is not defined");
        }
        instruction = new Instruction(this.pack, this, rawInstruction);
    }

    @Nullable
    private QuestPackage parsePackageFromIdentifier(@Nullable final QuestPackage pack, final String identifier, final int dotIndex) throws QuestException {
        final String packName = identifier.substring(0, dotIndex);
        if (pack != null) {
            if (packName.startsWith(UP_STR + "-")) {
                return resolveRelativePathUp(pack, identifier, packName);
            }
            if (packName.startsWith("-")) {
                return resolveRelativePathDown(pack, identifier, packName);
            }
        }
        final QuestPackage packFromDot = getDotIndex(identifier, packName);
        if (packFromDot != null) {
            return packFromDot;
        }
        if (identifier.length() == dotIndex + 1) {
            throw new QuestException("ID of the pack is null");
        }
        return null;
    }

    @Nullable
    private QuestPackage getDotIndex(final String identifier, final String packName) {
        final QuestPackage potentialPack = Config.getPackages().get(packName);
        if (potentialPack == null) {
            return null;
        }
        final String[] parts = identifier.split(":")[0].split("\\.");
        if (BetonQuest.getInstance().getQuestRegistries().variable().getFactory(packName) != null) {
            return resolveIdOfVariable(parts, potentialPack);
        }
        return potentialPack;
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private QuestPackage resolveRelativePathUp(final QuestPackage pack, final String identifier, final String packName) throws QuestException {
        final String[] root = pack.getQuestPath().split("-");
        final String[] path = packName.split("-");
        int stepsUp = 0;
        while (stepsUp < path.length && UP_STR.equals(path[stepsUp])) {
            stepsUp++;
        }
        if (stepsUp > root.length) {
            throw new QuestException("Relative path goes out of package scope! Consider removing a few '"
                    + UP_STR + "'s in ID " + identifier);
        }
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < root.length - stepsUp; i++) {
            builder.append(root[i]).append('-');
        }
        for (int i = stepsUp; i < path.length; i++) {
            builder.append(path[i]).append('-');
        }
        try {
            final String absolute = builder.substring(0, builder.length() - 1);
            final QuestPackage resolved = Config.getPackages().get(absolute);
            if (resolved == null) {
                throw new QuestException("Relative path in ID '" + identifier + "' resolved to '"
                        + absolute + "', but this package does not exist!");
            }
            return resolved;
        } catch (final StringIndexOutOfBoundsException e) {
            throw new QuestException("Relative path in ID '" + identifier + "' is invalid!", e);
        }
    }

    private QuestPackage resolveRelativePathDown(final QuestPackage pack, final String identifier, final String packName) throws QuestException {
        final String currentPath = pack.getQuestPath();
        final String fullPath = currentPath + packName;

        final QuestPackage resolved = Config.getPackages().get(fullPath);
        if (resolved == null) {
            throw new QuestException("Relative path in ID '" + identifier + "' resolved to '"
                    + fullPath + "', but this package does not exist!");
        }
        return resolved;
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    @Nullable
    private QuestPackage resolveIdOfVariable(final String[] parts, final QuestPackage potentialPack) {
        if (parts.length == 2 && isIdFromPack(potentialPack, parts[1])) {
            return potentialPack;
        } else if (parts.length > 2) {
            if (BetonQuest.getInstance().getQuestRegistries().variable().getFactory(parts[1]) != null
                    && isIdFromPack(potentialPack, parts[2])) {
                return potentialPack;
            } else if (isIdFromPack(potentialPack, parts[1])) {
                return null;
            } else {
                return potentialPack;
            }
        } else {
            return null;
        }
    }

    /**
     * Checks if an ID belongs to a provided QuestPackage. This checks all events, conditions, objectives and variables
     * for any ID matching the provided string
     *
     * @param pack       The quest package to search
     * @param identifier The id
     * @return true if the id exists in the quest package
     */
    private boolean isIdFromPack(final QuestPackage pack, final String identifier) {
        final MultiConfiguration config = pack.getConfig();
        for (final String path : PATHS) {
            if (config.getString(path + "." + identifier, null) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the package the object exist in.
     *
     * @return the package
     */
    public final QuestPackage getPackage() {
        return pack;
    }

    /**
     * Returns the base ID of the object. This is the ID without the package name.
     *
     * @return the base ID
     */
    public final String getBaseID() {
        return identifier;
    }

    /**
     * Returns the full ID of the object, which is in this format: <br>
     * <code>pack.identifier</code>
     *
     * @return the full ID
     */
    public final String getFullID() {
        return pack.getQuestPath() + "." + getBaseID();
    }

    /**
     * Returns the instruction of the object.
     *
     * @return the instruction
     * @throws IllegalStateException if the instruction is not set
     */
    public Instruction getInstruction() {
        if (instruction == null) {
            throw new IllegalStateException("Instruction is not set for ID " + getFullID());
        }
        return instruction;
    }

    @Override
    public String toString() {
        return getFullID();
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ID other = (ID) obj;
        return Objects.equals(identifier, other.identifier)
                && Objects.equals(pack.getQuestPath(), other.pack.getQuestPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, pack.getQuestPath());
    }
}
