package org.betonquest.betonquest.api;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.ObjectiveRegistry;
import org.betonquest.betonquest.api.service.BetonQuestConversations;
import org.betonquest.betonquest.api.service.BetonQuestInstructions;
import org.betonquest.betonquest.api.service.BetonQuestManagers;
import org.betonquest.betonquest.api.service.BetonQuestRegistries;
import org.betonquest.betonquest.api.service.ObjectiveManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * The BetonQuest API offers direct access to all methods related to BetonQuest.
 * Accessing and modifying the current state of BetonQuest will primarily be done through this api.
 * Getting an instance of this interface is done through the {@link BetonQuestApiServices}.
 * <br> <br>
 * The API is available and ready to use after BetonQuest itself has finished enabling and may therefore be called
 * the earliest while enabling a plugin explicitly depending on BetonQuest (enabling after BetonQuest).
 */
public interface BetonQuestApiInstance {

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
    ProfileProvider getProfiles();

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
    QuestPackageManager getPackages();

    /**
     * Offers functionality to create loggers that are integrated with BetonQuest.
     * <br> <br>
     * By creating a {@link BetonQuestLogger} for each class the filtering mechanism of BetonQuest can be used.
     * Additionally, setting a topic sometimes helps to assign log records to a specific part of the code.
     *
     * @return the logger factory offering functionality to create loggers
     * @see BetonQuestLogger
     */
    BetonQuestLoggerFactory getLoggers();

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
    BetonQuestInstructions getInstructions();

    /**
     * Offers functionality to access conversation in BetonQuest.
     * <br> <br>
     * Conversations are the fundamental concept underlying pretty much all interactions in BetonQuest.
     *
     * @return the conversation api offering functionality to access conversations
     */
    BetonQuestConversations getConversations();

    /**
     * Offers functionality to register custom features.
     * <br> <br>
     * A registry allows registering new implementations of factories for quest type objects in BetonQuest.
     * For example, to have a new type of objective, create an {@link ObjectiveFactory} and register it
     * using the {@link ObjectiveRegistry} accessible through {@link BetonQuestRegistries#getObjectives()}.
     *
     * @return the betonquest registries offering functionality to register custom features
     */
    BetonQuestRegistries getRegistries();

    /**
     * Offers functionality to access existing and loaded types.
     * <br> <br>
     * A manager allows accessing all instances created for a specific type.
     * For example, to start an objective with an {@link ObjectiveIdentifier} for a specific {@link OnlineProfile}, call
     * {@link BetonQuestManagers#getObjectives()} and use {@link ObjectiveManager#start(Profile, ObjectiveIdentifier)}.
     *
     * @return the betonquest managers offering functionality to access existing and loaded types
     */
    BetonQuestManagers getManagers();
}
