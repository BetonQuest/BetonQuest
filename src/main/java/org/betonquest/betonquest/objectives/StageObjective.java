package org.betonquest.betonquest.objectives;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.util.List;
import java.util.Locale;

/**
 * The StageObjective is a special objective that can be used to create a stage system for a quest.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class StageObjective extends Objective {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The mapping of stages to indices.
     */
    private final StageMap stageMap;

    /**
     * The stage that is used to complete the objective.
     */
    private final String completionStage;

    /**
     * True if the increase of stages should not complete the objective.
     */
    private final boolean preventCompletion;

    /**
     * The fallback stage that is used for the profile if the current stage is not a valid stage.
     */
    private final String fallbackStage;

    /**
     * Creates a new stage objective.
     *
     * @param instruction the instruction
     * @throws InstructionParseException if the instruction is invalid
     */
    public StageObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        log = BetonQuest.getInstance().getLoggerFactory().create(this.getClass());
        template = StageData.class;

        this.stageMap = new StageMap(instruction.getList(entry -> entry));
        this.completionStage = instruction.getOptional("completion");
        this.preventCompletion = instruction.hasArgument("preventCompletion");
        this.fallbackStage = instruction.getOptional("fallback");
        if (fallbackStage != null && !stageMap.isValidStage(fallbackStage)) {
            throw new InstructionParseException("Fallback stage must be one of the stages");
        }
    }

    @Override
    public void start() {
        // Empty
    }

    @Override
    public void stop() {
        // Empty
    }

    @Override
    public String getDefaultDataInstruction() {
        try {
            return stageMap.getStage(0);
        } catch (final QuestRuntimeException e) {
            log.reportException(instruction.getPackage(), e);
            return "";
        }
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        try {
            return switch (name.toLowerCase(Locale.ROOT)) {
                case "index" -> String.valueOf(stageMap.getIndex(getStage(profile)));
                case "current" -> getStage(profile);
                case "next" -> stageMap.nextStage(getStage(profile));
                case "previous" -> stageMap.previousStage(getStage(profile));
                default -> "";
            };
        } catch (final QuestRuntimeException e) {
            log.debug(instruction.getPackage(), "Error while getting property '" + name + "' for objective '" + instruction.getID() + "'.", e);
            return "";
        }
    }

    /**
     * Returns the stage of a profile.
     *
     * @param profile the profile
     * @return the stage
     * @throws QuestRuntimeException if the stage is not a valid stage for the objective
     */
    public String getStage(final Profile profile) throws QuestRuntimeException {
        final StageData stageData = (StageObjective.StageData) dataMap.get(profile);
        final String stage = stageData.getStage();
        if (stageMap.isValidStage(stage)) {
            return stage;
        }
        if (fallbackStage != null) {
            log.warn(instruction.getPackage(), profile + " has invalid stage '" + stage + "' for objective '" + instruction.getID() + "'. Using fallback stage '" + fallbackStage + "'.");
            stageData.setStage(fallbackStage);
            return fallbackStage;
        }
        throw new QuestRuntimeException(profile + " has invalid stage '" + stage + "' for objective '" + instruction.getID() + "'.");
    }

    /**
     * Sets the stage of a profile.
     *
     * @param profile the profile
     * @param stage   the stage
     * @throws QuestRuntimeException if the stage is not a valid stage for the objective
     */
    public void setStage(final Profile profile, final String stage) throws QuestRuntimeException {
        final StageData stageData = (StageObjective.StageData) dataMap.get(profile);
        if (stageMap.isValidStage(stage)) {
            stageData.setStage(stage);
            return;
        }
        if (stage.equals(completionStage)) {
            completeObjective(profile);
            return;
        }
        throw new QuestRuntimeException("Invalid stage '" + stage + "' for objective '" + instruction.getID() + "'.");
    }

    /**
     * Increases the stage of a profile.
     *
     * @param profile the profile
     * @throws QuestRuntimeException if the stage is not a valid stage for the objective
     */
    public void increaseStage(final Profile profile) throws QuestRuntimeException {
        final String stage = getStage(profile);
        final String nextStage;
        try {
            nextStage = stageMap.nextStage(stage);
        } catch (final QuestRuntimeException e) {
            if (!preventCompletion) {
                completeObjective(profile);
            }
            return;
        }
        setStage(profile, nextStage);
    }

    /**
     * Returns the index of the stage of a profile.
     *
     * @param stage the stage
     * @return the index of the stage
     * @throws QuestRuntimeException if the stage is not a valid stage for the objective
     */
    public int getStageIndex(final String stage) throws QuestRuntimeException {
        return stageMap.getIndex(stage);
    }

    /**
     * Decreases the stage of a profile.
     *
     * @param profile the profile
     * @throws QuestRuntimeException if the stage is not a valid stage for the objective
     */
    public void decreaseStage(final Profile profile) throws QuestRuntimeException {
        final String stage = getStage(profile);
        final String previousStage;
        try {
            previousStage = stageMap.previousStage(stage);
        } catch (final QuestRuntimeException e) {
            return;
        }
        setStage(profile, previousStage);
    }

    /**
     * {@link org.betonquest.betonquest.api.Objective.ObjectiveData} for {@link StageObjective}.
     */
    public static class StageData extends ObjectiveData {
        /**
         * Creates a new objective data.
         *
         * @param instruction the instruction
         * @param profile     the profile
         * @param objID       the objective ID
         */
        public StageData(final String instruction, final Profile profile, final String objID) {
            super(instruction, profile, objID);
        }

        /**
         * Returns the stage of the objective.
         *
         * @return the stage
         */
        private String getStage() {
            return instruction;
        }

        /**
         * Sets the stage of the objective.
         *
         * @param stage the stage
         */
        public void setStage(final String stage) {
            instruction = stage;
            update();
        }
    }

    /**
     * A mapping of stages to indices in a bidirectional map.
     */
    public class StageMap {
        /**
         * The mapping of stages to indices.
         */
        private final BiMap<String, Integer> stages;

        /**
         * Creates a new mapping of stages to indices.
         *
         * @param stages the stages
         * @throws InstructionParseException if there are duplicate stages or no stages
         */
        public StageMap(final List<String> stages) throws InstructionParseException {
            this.stages = HashBiMap.create(stages.size());
            for (int i = 0; i < stages.size(); i++) {
                final String key = stages.get(i);
                if (this.stages.containsKey(key)) {
                    throw new InstructionParseException("Duplicate stage '" + key + "'!");
                }
                this.stages.put(key, i);
            }
            if (this.stages.isEmpty()) {
                throw new InstructionParseException("No stages defined");
            }
        }

        /**
         * Returns the stage for the given index.
         *
         * @param index the index
         * @return the stage
         * @throws QuestRuntimeException if the index is not a valid index for the objective
         */
        public String getStage(final int index) throws QuestRuntimeException {
            final String stage = stages.inverse().get(index);
            if (stage == null) {
                throw new QuestRuntimeException("Index '" + index + "' is not a valid index for objective '" + instruction.getID() + "'.");
            }
            return stage;
        }

        /**
         * Returns the index of the stage.
         *
         * @param stage the stage
         * @return the index of the stage
         * @throws QuestRuntimeException if the stage is not a valid stage for the objective
         */
        public int getIndex(final String stage) throws QuestRuntimeException {
            final Integer index = stages.get(stage);
            if (index == null) {
                throw new QuestRuntimeException("Stage '" + stage + "' is not a valid stage for objective '" + instruction.getID() + "'.");
            }
            return index;
        }

        /**
         * Returns the next stage for the given stage.
         *
         * @param stage the stage
         * @return the next stage
         * @throws QuestRuntimeException if the stage is not a valid stage for the objective
         */
        public String nextStage(final String stage) throws QuestRuntimeException {
            return getStage(getIndex(stage) + 1);
        }

        /**
         * Returns the previous stage for the given stage.
         *
         * @param stage the stage
         * @return the previous stage
         * @throws QuestRuntimeException if the stage is not a valid stage for the objective
         */
        public String previousStage(final String stage) throws QuestRuntimeException {
            return getStage(getIndex(stage) - 1);
        }

        /**
         * Returns if the given stage is a valid stage for the objective.
         *
         * @param stage the stage
         * @return if the stage is a valid stage
         */
        public boolean isValidStage(final String stage) {
            return stages.containsKey(stage);
        }
    }
}
