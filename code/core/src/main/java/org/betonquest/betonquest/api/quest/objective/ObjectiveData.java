package org.betonquest.betonquest.api.quest.objective;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.bukkit.event.QuestDataUpdateEvent;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;

/**
 * Stores the profile's data for the objective.
 */
public class ObjectiveData {
    /**
     * Profile the data is for.
     */
    protected final Profile profile;

    /**
     * Full path of the ObjectiveID.
     */
    protected final ObjectiveID objID;

    /**
     * Instruction containing all required information.
     */
    protected String instruction;

    /**
     * The ObjectiveData object is loaded from the database and the
     * constructor needs to parse the data in the instruction, so it can be
     * later retrieved and modified by your objective code.
     *
     * @param instruction the instruction of the data object; parse it to get all
     *                    required information
     * @param profile     the {@link Profile} to load the data for
     * @param objID       ID of the objective, used by BetonQuest to store this
     *                    ObjectiveData in the database
     */
    public ObjectiveData(final String instruction, final Profile profile, final ObjectiveID objID) {
        this.instruction = instruction;
        this.profile = profile;
        this.objID = objID;
    }

    /**
     * This method should return the whole instruction string, which can be
     * successfully parsed by the constructor. This method is used by
     * BetonQuest to save the ObjectiveData to the database. That's why the
     * output syntax here must be compatible with input syntax in the
     * constructor.
     *
     * @return the instruction string
     */
    @Override
    public String toString() {
        return instruction;
    }

    /**
     * Should be called when the data inside ObjectiveData changes. It will
     * update the database with the changes.
     * <p>
     * If you forget it, the objective will still work for players who don't
     * leave the server. However, if someone leaves before completing, they
     * will have to start this objective from scratch.
     */
    protected final void update() {
        final BetonQuest plugin = BetonQuest.getInstance();
        final Saver saver = plugin.getSaver();
        saver.add(new Saver.Record(UpdateType.REMOVE_OBJECTIVES, profile.getProfileUUID().toString(), objID.getFull()));
        saver.add(new Saver.Record(UpdateType.ADD_OBJECTIVES, profile.getProfileUUID().toString(), objID.getFull(), toString()));
        final QuestDataUpdateEvent event = new QuestDataUpdateEvent(profile, objID, toString());
        plugin.getServer().getScheduler().runTask(plugin, event::callEvent);
        if (profile.getOnlineProfile().isPresent()) {
            plugin.getPlayerDataStorage().get(profile).getJournal().update();
        }
    }
}
