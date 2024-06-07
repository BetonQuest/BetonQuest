package org.betonquest.betonquest.quest.legacy;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.QuestFactory;
import org.betonquest.betonquest.api.quest.StaticQuestFactory;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.jetbrains.annotations.Nullable;

/**
 * Adapter to let {@link QuestFactory QuestFactories} create {@link L}s
 * from the {@link T}s and {@link S}' they create.
 *
 * @param <T> quest type
 * @param <S> static quest type
 * @param <L> legacy quest type
 */
public abstract class LegacyFactoryAdapter<T, S, L> implements LegacyTypeFactory<L> {
    /**
     * The event factory to be adapted.
     */
    @Nullable
    private final QuestFactory<T> factory;

    /**
     * The static event factory to be adapted.
     */
    @Nullable
    private final StaticQuestFactory<S> staticFactory;

    /**
     * Create the factory from an {@link EventFactory}.
     * <p>
     * When no normal factory is given the static factory is required.
     *
     * @param factory       event factory to use
     * @param staticFactory static event factory to use
     */
    public LegacyFactoryAdapter(@Nullable final QuestFactory<T> factory, @Nullable final StaticQuestFactory<S> staticFactory) {
        if (factory == null && staticFactory == null) {
            throw new IllegalArgumentException("Either the normal or static factory must be present!");
        }
        this.factory = factory;
        this.staticFactory = staticFactory;
    }

    @Override
    public L parseInstruction(final Instruction instruction) throws InstructionParseException {
        final T type = factory == null ? null : factory.parse(instruction.copy());
        final S staticType = staticFactory == null ? null : staticFactory.parseStatic(instruction.copy());
        return getAdapter(instruction, type, staticType);
    }

    /**
     * Creates a new adapter from the new API to the old system.
     * <p>
     * When no normal factory is given the static factory is required.
     *
     * @param instruction the instruction to store in the adapter, satisfying old needs
     * @param type        the normal type to adapt
     * @param staticType  the static type to adapt
     * @return the new adapter
     * @throws InstructionParseException when the instruction cannot be parsed
     */
    protected abstract L getAdapter(Instruction instruction, @Nullable T type, @Nullable S staticType) throws InstructionParseException;
}
