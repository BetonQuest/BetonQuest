package org.betonquest.betonquest.quest.placeholder.npc;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.NullablePlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholderFactory;
import org.betonquest.betonquest.api.quest.placeholder.nullable.NullablePlaceholderAdapter;
import org.betonquest.betonquest.quest.placeholder.location.LocationFormationMode;
import org.betonquest.betonquest.quest.placeholder.location.LocationPlaceholder;
import org.betonquest.betonquest.quest.placeholder.name.QuesterPlaceholder;

/**
 * Factory to create {@link NpcPlaceholder}s and {@link QuesterPlaceholder}s from {@link Instruction}s.
 * <p>
 * Format:
 * {@code %<placeholderName>.<id>.<argument>.<mode>.<precision>%}
 * <p>
 * Arguments:<br>
 * * name - Return npc name<br>
 * * full_name - Full npc name<br>
 * * location - Return npc location, defaults to ulfLong<br>
 * Modes: refer to LocationPlaceholder documentation for details.<br>
 *
 * @see LocationPlaceholder
 */
public class NpcPlaceholderFactory implements PlayerPlaceholderFactory, PlayerlessPlaceholderFactory {

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * Create a new factory to create NPC Placeholders.
     *
     * @param featureApi the Quest Type API
     */
    public NpcPlaceholderFactory(final FeatureApi featureApi) {
        this.featureApi = featureApi;
    }

    @Override
    public PlayerlessPlaceholder parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullablePlaceholderAdapter(parseInstruction(instruction));
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
        return new NullablePlaceholderAdapter(parseInstruction(instruction));
    }

    private NullablePlaceholder parseInstruction(final Instruction instruction) throws QuestException {
        if (!instruction.hasNext() || instruction.size() == 2 && "conversation".equals(instruction.getPart(1))) {
            final QuesterPlaceholder questerPlaceholder = new QuesterPlaceholder(featureApi.conversationApi());
            return profile -> {
                if (profile == null) {
                    throw new QuestException("Profile can't be null for conversation!");
                }
                return questerPlaceholder.getValue(profile);
            };
        }
        final Argument<NpcIdentifier> npcID = instruction.identifier(NpcIdentifier.class).get();
        final NPCArgument key = instruction.enumeration(NPCArgument.class).get().getValue(null);
        LocationFormationMode locationFormationMode = null;
        int decimalPlaces = 0;
        if (key == NPCArgument.LOCATION) {
            if (instruction.hasNext()) {
                locationFormationMode = LocationFormationMode.getMode(instruction.nextElement());
            } else {
                locationFormationMode = LocationFormationMode.ULF_LONG;
            }
            if (instruction.hasNext()) {
                decimalPlaces = Integer.parseInt(instruction.nextElement());
            }
        }
        return new NpcPlaceholder(featureApi, npcID, key, locationFormationMode, decimalPlaces);
    }
}
