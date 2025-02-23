package org.betonquest.betonquest.config.patcher;

import org.betonquest.betonquest.api.config.patcher.PatchTransformer;
import org.betonquest.betonquest.api.config.patcher.PatchTransformerRegistry;
import org.betonquest.betonquest.config.patcher.transformer.KeyRenameTransformer;
import org.betonquest.betonquest.config.patcher.transformer.ListEntryAddTransformer;
import org.betonquest.betonquest.config.patcher.transformer.ListEntryRemoveTransformer;
import org.betonquest.betonquest.config.patcher.transformer.ListEntryRenameTransformer;
import org.betonquest.betonquest.config.patcher.transformer.RemoveTransformer;
import org.betonquest.betonquest.config.patcher.transformer.SetTransformer;
import org.betonquest.betonquest.config.patcher.transformer.TypeTransformer;
import org.betonquest.betonquest.config.patcher.transformer.ValueRenameTransformer;
import org.betonquest.betonquest.config.patcher.transformer.ValueReplaceTransformer;

import java.util.Map;

/**
 * Default implementation of {@link PatchTransformerRegistry} containing all transformers of BetonQuest.
 */
public class DefaultPatchTransformerRegistry implements PatchTransformerRegistry {
    /**
     * A map of transformers to use for patching and their names.
     */
    private final Map<String, PatchTransformer> transformers;

    /**
     * Creates a new {@link DefaultPatchTransformerRegistry}.
     */
    public DefaultPatchTransformerRegistry() {
        transformers = Map.of("SET", new SetTransformer(),
                "REMOVE", new RemoveTransformer(),
                "KEY_RENAME", new KeyRenameTransformer(),
                "VALUE_RENAME", new ValueRenameTransformer(),
                "VALUE_REPLACE", new ValueReplaceTransformer(),
                "LIST_ENTRY_ADD", new ListEntryAddTransformer(),
                "LIST_ENTRY_REMOVE", new ListEntryRemoveTransformer(),
                "LIST_ENTRY_RENAME", new ListEntryRenameTransformer(),
                "TYPE_TRANSFORM", new TypeTransformer());
    }

    @Override
    public Map<String, PatchTransformer> getTransformers() {
        return transformers;
    }
}
