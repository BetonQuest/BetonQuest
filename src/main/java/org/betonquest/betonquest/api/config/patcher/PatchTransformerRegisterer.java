package org.betonquest.betonquest.api.config.patcher;

import org.betonquest.betonquest.modules.config.Patcher;
import org.betonquest.betonquest.modules.config.transformers.KeyRenameTransformer;
import org.betonquest.betonquest.modules.config.transformers.ListEntryAddTransformer;
import org.betonquest.betonquest.modules.config.transformers.ListEntryRemoveTransformer;
import org.betonquest.betonquest.modules.config.transformers.ListEntryRenameTransformer;
import org.betonquest.betonquest.modules.config.transformers.RemoveTransformer;
import org.betonquest.betonquest.modules.config.transformers.SetTransformer;
import org.betonquest.betonquest.modules.config.transformers.TypeTransformer;
import org.betonquest.betonquest.modules.config.transformers.ValueRenameTransformer;

/**
 * Functional interface for registering all transformers of a {@link Patcher}.
 */
public interface PatchTransformerRegisterer {

    /**
     * Functional interface for registering all transformers of a {@link Patcher}.
     *
     * @param patcher the {@link Patcher} to register the transformers for
     */
    default void registerTransformers(final Patcher patcher) {
        patcher.registerTransformer("SET", new SetTransformer());
        patcher.registerTransformer("REMOVE", new RemoveTransformer());
        patcher.registerTransformer("KEY_RENAME", new KeyRenameTransformer());
        patcher.registerTransformer("VALUE_RENAME", new ValueRenameTransformer());
        patcher.registerTransformer("LIST_ENTRY_ADD", new ListEntryAddTransformer());
        patcher.registerTransformer("LIST_ENTRY_REMOVE", new ListEntryRemoveTransformer());
        patcher.registerTransformer("LIST_ENTRY_RENAME", new ListEntryRenameTransformer());
        patcher.registerTransformer("TYPE_TRANSFORM", new TypeTransformer());
    }
}

