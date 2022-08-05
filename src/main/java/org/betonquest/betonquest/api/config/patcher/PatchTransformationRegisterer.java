package org.betonquest.betonquest.api.config.patcher;

import org.betonquest.betonquest.modules.config.Patcher;
import org.betonquest.betonquest.modules.config.transformers.KeyRenameTransformation;
import org.betonquest.betonquest.modules.config.transformers.ListEntryAddTransformation;
import org.betonquest.betonquest.modules.config.transformers.ListEntryRemoveTransformation;
import org.betonquest.betonquest.modules.config.transformers.ListEntryRenameTransformation;
import org.betonquest.betonquest.modules.config.transformers.RemoveTransformation;
import org.betonquest.betonquest.modules.config.transformers.SetTransformation;
import org.betonquest.betonquest.modules.config.transformers.ValueRenameTransformation;

/**
 * Functional interface for registering all transformations of a {@link Patcher}.
 */
public interface PatchTransformationRegisterer {

    /**
     * Functional interface for registering all transformations of a {@link Patcher}.
     *
     * @param patcher the {@link Patcher} to register the transformations for
     */
    default void registerTransformations(final Patcher patcher) {
        patcher.registerTransformer("SET", new SetTransformation());
        patcher.registerTransformer("REMOVE", new RemoveTransformation());
        patcher.registerTransformer("KEY_RENAME", new KeyRenameTransformation());
        patcher.registerTransformer("VALUE_RENAME", new ValueRenameTransformation());
        patcher.registerTransformer("LIST_ENTRY_ADD", new ListEntryAddTransformation());
        patcher.registerTransformer("LIST_ENTRY_REMOVE", new ListEntryRemoveTransformation());
        patcher.registerTransformer("LIST_ENTRY_RENAME", new ListEntryRenameTransformation());
    }
}

