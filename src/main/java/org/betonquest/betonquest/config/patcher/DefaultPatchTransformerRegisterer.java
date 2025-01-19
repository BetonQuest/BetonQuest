package org.betonquest.betonquest.config.patcher;

import org.betonquest.betonquest.api.config.patcher.PatchTransformerRegisterer;
import org.betonquest.betonquest.config.Patcher;
import org.betonquest.betonquest.config.patcher.transformers.KeyRenameTransformer;
import org.betonquest.betonquest.config.patcher.transformers.ListEntryAddTransformer;
import org.betonquest.betonquest.config.patcher.transformers.ListEntryRemoveTransformer;
import org.betonquest.betonquest.config.patcher.transformers.ListEntryRenameTransformer;
import org.betonquest.betonquest.config.patcher.transformers.RemoveTransformer;
import org.betonquest.betonquest.config.patcher.transformers.SetTransformer;
import org.betonquest.betonquest.config.patcher.transformers.TypeTransformer;
import org.betonquest.betonquest.config.patcher.transformers.ValueRenameTransformer;

/**
 * Default implementation of {@link PatchTransformerRegisterer} containing all transformers of BetonQuest.
 */
public class DefaultPatchTransformerRegisterer implements PatchTransformerRegisterer {
    /**
     * Creates a new {@link DefaultPatchTransformerRegisterer}.
     */
    public DefaultPatchTransformerRegisterer() {
    }

    @Override
    public void registerTransformers(final Patcher patcher) {
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
