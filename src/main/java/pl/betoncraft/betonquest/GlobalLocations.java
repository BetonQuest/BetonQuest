package pl.betoncraft.betonquest;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.id.EventID;
import pl.betoncraft.betonquest.id.ObjectiveID;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.location.CompoundLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

/**
 * Handler for global locations.
 *
 * @deprecated The old global locations system got replaced by the new {@link GlobalObjectives global objectives},
 * this will be removed in 2.0 release
 */
// TODO Delete in BQ 2.0.0
@Deprecated
@SuppressWarnings("PMD.CommentRequired")
public class GlobalLocations extends BukkitRunnable {

    private static GlobalLocations instance;
    private final List<GlobalLocation> finalLocations;
    private final List<GlobalLocation> locations = new ArrayList<>();

    /**
     * Creates new instance of global locations handler.
     */
    @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
    @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    public GlobalLocations() {
        super();
        // cancel previous instance if it exists
        if (instance != null) {
            stop();
        }
        instance = this;
        // get list of global locations and make it final
        for (final ConfigPackage pack : Config.getPackages().values()) {
            final String rawGlobalLocations = pack.getString("main.global_locations");
            if (rawGlobalLocations == null || rawGlobalLocations.equals("")) {
                continue;
            }
            final String[] parts = rawGlobalLocations.split(",");
            for (final String objective : parts) {
                try {
                    final ObjectiveID objectiveID = new ObjectiveID(pack, objective);
                    final GlobalLocation location = new GlobalLocation(objectiveID);
                    locations.add(location);
                } catch (ObjectNotFoundException | InstructionParseException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Error while parsing global location objective '" + objective + "': " + e.getMessage());
                    LogUtils.logThrowable(e);
                }
            }
        }
        finalLocations = locations;
        runTaskTimer(BetonQuest.getInstance(), 20, 20);
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @Override
    public void run() {
        // do nothing if there is no defined locations
        if (finalLocations == null) {
            this.cancel();
            return;
        }
        // loop all online players
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (final Player player : players) {
            final String playerID = PlayerConverter.getID(player);
            // for each player loop all available locations
            for (final GlobalLocation location : finalLocations) {
                // if location is not set, stop everything, there is an error in config
                if (location.getLocation() == null) {
                    continue;
                }
                // if player is inside location, do stuff
                final Location loc;
                final double distance;
                try {
                    loc = location.getLocation().getLocation(playerID);
                    distance = location.getRange().getDouble(playerID);
                } catch (QuestRuntimeException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Error while parsing location in global location '" + location.getObjectiveID()
                            + "': " + e.getMessage());
                    LogUtils.logThrowable(e);
                    continue;
                }
                if (player.getLocation().getWorld().equals(loc.getWorld())
                        && player.getLocation().distanceSquared(loc) <= distance * distance) {
                    // check if player has already triggered this location
                    final PlayerData playerData = BetonQuest.getInstance().getPlayerData(playerID);
                    if (playerData.hasTag(location.getTag())) {
                        continue;
                    }
                    // check all conditions
                    if (location.getConditions() != null && !BetonQuest.conditions(playerID, location.getConditions())) {
                        continue;
                    }
                    // set the tag, player has triggered this location
                    playerData.addTag(location.getTag());
                    // fire all events for the location
                    for (final EventID event : location.getEvents()) {
                        BetonQuest.event(playerID, event);
                    }
                }
            }
        }
    }

    /**
     * Stops active global locations timer
     */
    public static void stop() {
        instance.cancel();
    }

    /**
     * Represents single global location.
     */
    @SuppressWarnings("PMD.DataClass")
    private static class GlobalLocation {

        private final ObjectiveID objectiveID;
        private final CompoundLocation location;
        private final VariableNumber range;
        private final ConditionID[] conditions;
        private final EventID[] events;
        private final String tag;

        /**
         * Creates new global location using objective event's ID.
         *
         * @param objectiveID ID of the event
         * @throws InstructionParseException
         */
        public GlobalLocation(final ObjectiveID objectiveID) throws InstructionParseException {
            this.objectiveID = objectiveID;
            LogUtils.getLogger().log(Level.FINE, "Creating new GlobalLocation from " + objectiveID + " event.");
            final Instruction instruction = objectiveID.generateInstruction();
            // check amount of arguments in event's instruction
            location = instruction.getLocation();
            range = instruction.getVarNum();
            // extract all conditions and events
            final String[] tempConditions1 = instruction.getArray(instruction.getOptional("condition"));
            final String[] tempConditions2 = instruction.getArray(instruction.getOptional("conditions"));
            int length = tempConditions1.length + tempConditions2.length;
            conditions = new ConditionID[length];
            for (int i = 0; i < length; i++) {
                final String condition = i >= tempConditions1.length ? tempConditions2[i - tempConditions1.length] : tempConditions1[i];
                try {
                    conditions[i] = new ConditionID(instruction.getPackage(), condition);
                } catch (ObjectNotFoundException e) {
                    throw new InstructionParseException("Error while parsing event conditions: " + e.getMessage(), e);
                }
            }
            final String[] tempEvents1 = instruction.getArray(instruction.getOptional("event"));
            final String[] tempEvents2 = instruction.getArray(instruction.getOptional("events"));
            length = tempEvents1.length + tempEvents2.length;
            events = new EventID[length];
            for (int i = 0; i < length; i++) {
                final String event = i >= tempEvents1.length ? tempEvents2[i - tempEvents1.length] : tempEvents1[i];
                try {
                    events[i] = new EventID(instruction.getPackage(), event);
                } catch (ObjectNotFoundException e) {
                    throw new InstructionParseException("Error while parsing objective events: " + e.getMessage(), e);
                }
            }
            tag = objectiveID.getPackage().getName() + ".global_" + objectiveID;
        }

        /**
         * @return the objectiveID of this global location
         */
        public ObjectiveID getObjectiveID() {
            return objectiveID;
        }

        /**
         * @return the location
         */
        public CompoundLocation getLocation() {
            return location;
        }

        /**
         * @return the range variable
         */
        public VariableNumber getRange() {
            return range;
        }

        /**
         * @return the conditions
         */
        public ConditionID[] getConditions() {
            return Arrays.copyOf(conditions, conditions.length);
        }

        /**
         * @return the events
         */
        public EventID[] getEvents() {
            return Arrays.copyOf(events, events.length);
        }

        /**
         * @return the tag required to pass this global location
         */
        public String getTag() {
            return tag;
        }
    }
}
