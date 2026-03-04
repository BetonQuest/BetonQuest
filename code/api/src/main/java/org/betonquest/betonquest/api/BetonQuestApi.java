package org.betonquest.betonquest.api;

import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.config.section.multi.MultiConfiguration;
import org.betonquest.betonquest.api.data.Persistence;
import org.betonquest.betonquest.api.data.PersistentDataHolder;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.reload.Reloader;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.action.ActionRegistry;
import org.betonquest.betonquest.api.service.action.Actions;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.condition.ConditionRegistry;
import org.betonquest.betonquest.api.service.condition.Conditions;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.item.ItemManager;
import org.betonquest.betonquest.api.service.item.ItemRegistry;
import org.betonquest.betonquest.api.service.item.Items;
import org.betonquest.betonquest.api.service.npc.NpcManager;
import org.betonquest.betonquest.api.service.npc.NpcRegistry;
import org.betonquest.betonquest.api.service.npc.Npcs;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.api.service.objective.ObjectiveRegistry;
import org.betonquest.betonquest.api.service.objective.Objectives;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderRegistry;
import org.betonquest.betonquest.api.service.placeholder.Placeholders;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * The BetonQuest API offers direct access to all methods related to BetonQuest.
 * Accessing and modifying the current state of BetonQuest will primarily be done through this api.
 * Getting an instance of this interface is done through the {@link BetonQuestApiService}.
 * <br> <br>
 * The API is available and ready to use after BetonQuest itself has finished enabling and may therefore be called
 * the earliest while enabling a plugin explicitly depending on BetonQuest (enabling after BetonQuest).
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface BetonQuestApi {

    /**
     * Offers functionality to retrieve profiles for {@link Player}s, {@link OfflinePlayer}s and {@link UUID}s.
     * <br> <br>
     * Profiles are the BetonQuest representation of players and their quest-related data.
     * A profile always belongs to a player, but a player may have multiple profiles.
     *
     * @return the profile provider offering functionality to retrieve profiles
     * @see Profile
     * @see OnlineProfile
     */
    ProfileProvider profiles();

    /**
     * Offers functionality to retrieve {@link QuestPackage}s.
     * <br> <br>
     * Quest packages are the BetonQuest representation of a folder containing a quest configuration potentially split
     * into multiple files and being loaded into a {@link MultiConfiguration}.
     * The manager enables accessing said packages.
     *
     * @return the package manager offering functionality to retrieve packages
     * @see QuestPackage
     */
    QuestPackageManager packages();

    /**
     * Offers functionality to create loggers that are integrated with BetonQuest.
     * <br> <br>
     * By creating a {@link BetonQuestLogger} for each class the filtering mechanism of BetonQuest can be used.
     * Additionally, setting a topic sometimes helps to assign log records to a specific part of the code.
     *
     * @return the logger factory offering functionality to create loggers
     * @see BetonQuestLogger
     */
    BetonQuestLoggerFactory loggerFactory();

    /**
     * Offers functionality to create instructions to parse values containing placeholders.
     * <br> <br>
     * {@link Instruction}s may be used to parse strings into java objects using {@link InstructionArgumentParser}
     * via a chain of calls defining the parsing process.
     * A {@link SectionInstruction} does essentially the same but allows to parse a configuration section instead.
     * Parsing only a single value into an {@link Argument} is also possible.
     *
     * @return the instruction factory offering functionality to create instructions
     * @see Instruction
     * @see SectionInstruction
     */
    Instructions instructions();

    /**
     * Offers functionality to access conversation in BetonQuest.
     * <br> <br>
     * Conversations are the fundamental concept underlying pretty much all interactions in BetonQuest.
     *
     * @return the conversation api offering functionality to access conversations
     */
    Conversations conversations();

    /**
     * Offers functionality to access identifiers in BetonQuest.
     * <br> <br>
     * Identifiers are used to uniquely identify actions, conditions, objectives, placeholders, and so on.
     *
     * @return the identifier api offering functionality to access identifiers
     */
    Identifiers identifiers();

    /**
     * Offers functionality to access actions in BetonQuest.
     * <br> <br>
     * The {@link ActionRegistry} enables registering custom actions.
     * The {@link ActionManager} enables executing actions defined in BetonQuest.
     *
     * @return the action api offering functionality to access actions
     */
    Actions actions();

    /**
     * Offers functionality to access conditions in BetonQuest.
     * <br> <br>
     * The {@link ConditionRegistry} enables registering custom conditions.
     * The {@link ConditionManager} enables evaluating conditions defined in BetonQuest.
     *
     * @return the condition api offering functionality to access conditions
     */
    Conditions conditions();

    /**
     * Offers functionality to access objectives in BetonQuest.
     * <br> <br>
     * The {@link ObjectiveRegistry} enables registering custom objectives.
     * The {@link ObjectiveManager} enables managing objectives defined in BetonQuest.
     *
     * @return the objective api offering functionality to access objectives
     */
    Objectives objectives();

    /**
     * Offers functionality to access placeholders in BetonQuest.
     * <br> <br>
     * The {@link PlaceholderRegistry} enables registering custom placeholders.
     * The {@link PlaceholderManager} enables resolving placeholders defined in BetonQuest.
     *
     * @return the placeholder api offering functionality to access placeholders
     */
    Placeholders placeholders();

    /**
     * Offers functionality to access items in BetonQuest.
     * <br> <br>
     * The {@link ItemRegistry} enables registering custom items.
     * The {@link ItemManager} enables managing items defined in BetonQuest.
     *
     * @return the item api offering functionality to access items
     */
    Items items();

    /**
     * Offers functionality to access NPCs in BetonQuest.
     * <br> <br>
     * The {@link NpcRegistry} enables registering custom NPCs.
     * The {@link NpcManager} enables managing NPCs defined in BetonQuest.
     *
     * @return the npc api offering functionality to access NPCs
     */
    Npcs npcs();

    /**
     * Offers access to the font registry.
     * <br> <br>
     * The {@link FontRegistry} allows to register custom fonts used internally by BetonQuest for line wrapping.
     * It also grants access to existing fonts and their specifications.
     *
     * @return the font registry offering access to fonts
     */
    FontRegistry fonts();

    /**
     * Offers access to the reloader.
     * <br> <br>
     * The {@link Reloader} handles all functionality related to reloading BetonQuest.
     * By hooking functions into the Reloader, you may have your own tools reloaded in sync with BetonQuest.
     *
     * @return the reloader offering access to reloading
     */
    Reloader reloader();

    /**
     * Offers access to persistent data within BetonQuest.
     * <br> <br>
     * The {@link Persistence} manages all persistent information within BetonQuest and its database.
     * By accessing specific {@link PersistentDataHolder}s you may read or change persistent data in a specific context.
     *
     * @return the persistence offering access to persistent data
     */
    Persistence persistence();
}
