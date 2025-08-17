package org.betonquest.betonquest.kernel.processor.feature;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.instruction.variable.VariableList;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.feature.journal.JournalMainPageEntry;
import org.betonquest.betonquest.id.JournalMainPageID;
import org.betonquest.betonquest.kernel.processor.SectionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * Loads and stores Journal Main Pages.
 */
public class JournalMainPageProcessor extends SectionProcessor<JournalMainPageID, JournalMainPageEntry> {
    /**
     * The quest package manager to use for the instruction.
     */
    private final QuestPackageManager questPackageManager;

    /**
     * Variable to resolve conditions.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Text creator to parse text.
     */
    private final ParsedSectionTextCreator textCreator;

    /**
     * Create a new QuestProcessor to store and execute type logic.
     *
     * @param log                 the custom logger for this class
     * @param questPackageManager the quest package manager to use for the instruction
     * @param variableProcessor   the variable resolver to resolve conditions
     * @param textCreator         the text creator to parse text
     */
    public JournalMainPageProcessor(final BetonQuestLogger log, final QuestPackageManager questPackageManager, final VariableProcessor variableProcessor,
                                    final ParsedSectionTextCreator textCreator) {
        super(log, "Journal Main Page", "journal_main_page");
        this.questPackageManager = questPackageManager;
        this.variableProcessor = variableProcessor;
        this.textCreator = textCreator;
    }

    @Override
    protected JournalMainPageEntry loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final int priority = section.getInt("priority", -1);
        if (priority < 0) {
            throw new QuestException("Priority of journal main page needs to be at least 0!");
        }
        final Variable<List<ConditionID>> conditions = new VariableList<>(variableProcessor, pack,
                section.getString("conditions", ""),
                value -> new ConditionID(questPackageManager, pack, value));
        final Text text = textCreator.parseFromSection(pack, section, "text");
        return new JournalMainPageEntry(priority, conditions, text);
    }

    @Override
    protected JournalMainPageID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new JournalMainPageID(questPackageManager, pack, identifier);
    }
}
