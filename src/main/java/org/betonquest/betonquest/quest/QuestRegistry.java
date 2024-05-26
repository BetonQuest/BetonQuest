package org.betonquest.betonquest.quest;

import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.bstats.CompositeInstructionMetricsSupplier;
import org.betonquest.betonquest.bstats.InstructionMetricsSupplier;
import org.betonquest.betonquest.config.QuestCanceler;
import org.betonquest.betonquest.conversation.ConversationData;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.id.QuestCancelerID;
import org.betonquest.betonquest.id.VariableID;
import org.betonquest.betonquest.quest.event.legacy.QuestEventFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores the active Quest Types, Conversations and Quest Canceller.
 */
public class QuestRegistry {
    /**
     * Loaded Conditions.
     */
    public final Map<ConditionID, Condition> conditions = new HashMap<>();

    /**
     * Loaded Events.
     */
    public final Map<EventID, QuestEvent> events = new HashMap<>();

    /**
     * Loaded Objectives.
     */
    public final Map<ObjectiveID, Objective> objectives = new HashMap<>();

    /**
     * Loaded Variables.
     */
    public final Map<VariableID, Variable> variables = new HashMap<>();

    /**
     * Loaded Conversations.
     */
    public final Map<ConversationID, ConversationData> conversations = new HashMap<>();

    /**
     * Loaded Quest Canceller.
     */
    public final Map<QuestCancelerID, QuestCanceler> cancelers = new HashMap<>();

    /**
     * Available Condition types.
     */
    private final Map<String, Class<? extends Condition>> conditionTypes;

    /**
     * Available Event types.
     */
    private final Map<String, QuestEventFactory> eventTypes;

    /**
     * Available Objective types.
     */
    private final Map<String, Class<? extends Objective>> objectiveTypes;

    /**
     * Available Variable types.
     */
    private final Map<String, Class<? extends Variable>> variableTypes;

    /**
     * Create a new Registry for storing and using Conditions, Events, Objectives, Variables,
     * Conversations and Quest canceller.
     *
     * @param conditionTypes the available condition types
     * @param eventTypes     the available event types
     * @param objectiveTypes the available objective types
     * @param variableTypes  the available variable types
     */
    public QuestRegistry(final Map<String, Class<? extends Condition>> conditionTypes, final Map<String, QuestEventFactory> eventTypes,
                         final Map<String, Class<? extends Objective>> objectiveTypes, final Map<String, Class<? extends Variable>> variableTypes) {
        this.conditionTypes = conditionTypes;
        this.eventTypes = eventTypes;
        this.objectiveTypes = objectiveTypes;
        this.variableTypes = variableTypes;
    }

    /**
     * Clears the loaded data.
     */
    public void clear() {
        conditions.clear();
        conversations.clear();
        objectives.clear();
        variables.clear();
        cancelers.clear();
    }

    /**
     * Gets the bstats metric supplier for registered and active quest types.
     *
     * @return instruction metrics for conditions, events, objectives and variables
     */
    public Map<String, InstructionMetricsSupplier<? extends ID>> metricsSupplier() {
        final Map<String, InstructionMetricsSupplier<? extends ID>> metricsSuppliers = new HashMap<>();
        metricsSuppliers.put("conditions", new CompositeInstructionMetricsSupplier<>(conditions::keySet, conditionTypes::keySet));
        metricsSuppliers.put("events", new CompositeInstructionMetricsSupplier<>(events::keySet, eventTypes::keySet));
        metricsSuppliers.put("objectives", new CompositeInstructionMetricsSupplier<>(objectives::keySet, objectiveTypes::keySet));
        metricsSuppliers.put("variables", new CompositeInstructionMetricsSupplier<>(variables::keySet, variableTypes::keySet));
        return metricsSuppliers;
    }
}
