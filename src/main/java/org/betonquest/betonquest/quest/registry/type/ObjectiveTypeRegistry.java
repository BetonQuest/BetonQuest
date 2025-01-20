package org.betonquest.betonquest.quest.registry.type;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.registry.FromClassFactoryRegistry;

import java.lang.reflect.Constructor;

/**
 * Stores the Objectives that can be used in BetonQuest.
 */
public class ObjectiveTypeRegistry extends FromClassFactoryRegistry<Objective, TypeFactory<Objective>> {

    /**
     * Create a new Objective registry.
     *
     * @param log the logger that will be used for logging
     */
    public ObjectiveTypeRegistry(final BetonQuestLogger log) {
        super(log, "objective");
    }

    @Override
    protected TypeFactory<Objective> createFactory(final Class<? extends Objective> clazz) throws NoSuchMethodException {
        return new FactoryImpl(clazz.getConstructor(Instruction.class));
    }

    /**
     * Class Constructor based implementation.
     *
     * @param constructor the used constructor
     */
    private record FactoryImpl(Constructor<? extends Objective> constructor) implements TypeFactory<Objective> {

        @Override
        public Objective parseInstruction(final Instruction instruction) throws QuestException {
            return catchConstructionException("objective", constructor, instruction);
        }
    }
}
