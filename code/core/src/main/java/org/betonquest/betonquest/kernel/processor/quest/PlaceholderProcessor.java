package org.betonquest.betonquest.kernel.processor.quest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.InstructionApi;
import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.kernel.processor.TypedQuestProcessor;
import org.betonquest.betonquest.kernel.processor.adapter.PlaceholderAdapter;
import org.betonquest.betonquest.kernel.registry.quest.PlaceholderTypeRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;

/**
 * Stores Placeholders and resolve them.
 */
public class PlaceholderProcessor extends TypedQuestProcessor<PlaceholderIdentifier, PlaceholderAdapter> implements Placeholders {

    /**
     * Empty placeholders to satisfy the object structure.
     */
    public static final Placeholders EMPTY_PLACEHOLDER = new Placeholders() {
        @Override
        public PlaceholderAdapter create(@Nullable final QuestPackage pack, final String instruction) throws QuestException {
            throw new QuestException("Not valid");
        }

        @Override
        public String getValue(final QuestPackage pack, final String name, @Nullable final Profile profile) throws QuestException {
            throw new QuestException("Not valid");
        }

        @Override
        public String getValue(final String placeholder, @Nullable final Profile profile) throws QuestException {
            throw new QuestException("Not valid");
        }
    };

    /**
     * The Bukkit scheduler to run sync tasks.
     */
    private final BukkitScheduler scheduler;

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Create a new {@link Placeholders} to store placeholders, resolves them and create new.
     *
     * @param log                          the custom logger for this class
     * @param packManager                  the quest package manager to get quest packages from
     * @param placeholderTypes             the available placeholder types
     * @param scheduler                    the bukkit scheduler to run sync tasks
     * @param placeholderIdentifierFactory the factory to create placeholder identifiers
     * @param instructionApi               the instruction api
     * @param plugin                       the plugin instance
     */
    public PlaceholderProcessor(final BetonQuestLogger log, final QuestPackageManager packManager,
                                final PlaceholderTypeRegistry placeholderTypes, final BukkitScheduler scheduler,
                                final IdentifierFactory<PlaceholderIdentifier> placeholderIdentifierFactory,
                                final InstructionApi instructionApi, final Plugin plugin) {
        super(log, EMPTY_PLACEHOLDER, packManager, placeholderTypes, placeholderIdentifierFactory,
                instructionApi, "Placeholders", "placeholders");
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public void load(final QuestPackage pack) {
        // Empty
    }

    @Override
    public PlaceholderAdapter create(@Nullable final QuestPackage pack, final String instruction)
            throws QuestException {
        final PlaceholderIdentifier placeholderID;
        try {
            placeholderID = getIdentifier(pack, instruction);
        } catch (final QuestException e) {
            throw new QuestException("Could not load placeholder: " + e.getMessage(), e);
        }
        final PlaceholderAdapter existingPlaceholder = values.get(placeholderID);
        if (existingPlaceholder != null) {
            return existingPlaceholder;
        }
        final Instruction instructionVar = instructionApi.createInstruction(placeholderID, placeholderID.readRawInstruction());
        final TypeFactory<PlaceholderAdapter> placeholderFactory = types.getFactory(instructionVar.current());
        final PlaceholderAdapter placeholder = placeholderFactory.parseInstruction(instructionVar);
        values.put(placeholderID, placeholder);
        log.debug(pack, "Placeholder " + placeholderID + " loaded");
        return placeholder;
    }

    @Override
    public String getValue(final QuestPackage pack, final String name, @Nullable final Profile profile) throws QuestException {
        final PlaceholderAdapter placeholder;
        try {
            placeholder = create(pack, name);
        } catch (final QuestException e) {
            throw new QuestException("Could not create placeholder '" + name + "': " + e.getMessage(), e);
        }
        if (placeholder.isPrimaryThreadEnforced() && !Bukkit.isPrimaryThread()) {
            return valueSync(profile, name, placeholder);
        }
        return value(profile, name, placeholder);
    }

    @Override
    public String getValue(final String placeholder, @Nullable final Profile profile) throws QuestException {
        final int index = placeholder.indexOf(':');
        if (index == -1) {
            throw new QuestException("Placeholder without explicit package '" + placeholder + "'! Expected format '<package>:<placeholder>'");
        }
        final String packString = placeholder.substring(0, index);
        final QuestPackage pack = packManager.getPackage(packString);
        if (pack == null) {
            throw new QuestException("The placeholder '" + placeholder + "' reference the non-existent package '" + packString + "' !");
        }
        final String value = placeholder.substring(index + 1);
        return getValue(pack, '%' + value + '%', profile);
    }

    private String valueSync(@Nullable final Profile profile, final String placeholderString, final PlaceholderAdapter placeholder) throws QuestException {
        try {
            return scheduler.callSyncMethod(plugin, () -> value(profile, placeholderString, placeholder)).get();
        } catch (final InterruptedException | ExecutionException e) {
            log.reportException(e);
            throw new QuestException(e);
        }
    }

    private String value(@Nullable final Profile profile, final String placeholderString, final PlaceholderAdapter placeholder) throws QuestException {
        try {
            return placeholder.getValue(profile);
        } catch (final QuestException e) {
            throw new QuestException("Error while resolving '" + placeholderString + "' placeholder: " + e.getMessage(), e);
        }
    }
}
