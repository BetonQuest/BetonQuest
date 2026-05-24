package org.betonquest.betonquest.compatibility.mmogroup.mmolib;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatInstance;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.jetbrains.annotations.Nullable;

/**
 * Action to change a stat of a player.
 */
public class MythicLibStatAction implements PlayerAction {

    /**
     * The name of the stat to change.
     */
    @Nullable
    private final Argument<String> statName;

    /**
     * The value to set the stat to.
     */
    private final Argument<Number> value;

    /**
     * The key of the stat modifier.
     */
    private final Argument<String> modifierKey;

    /**
     * The type of the stat modifier.
     */
    private final Argument<ModifierType> type;

    /**
     * The slot of the stat modifier.
     */
    private final Argument<EquipmentSlot> slot;

    /**
     * The source of the stat modifier.
     */
    private final Argument<ModifierSource> source;

    /**
     * Flag to determine whether to add the stat modifier.
     */
    private final FlagArgument<Boolean> add;

    /**
     * Flag to determine whether to remove the stat modifier.
     */
    private final FlagArgument<Boolean> remove;

    /**
     * Flag to determine whether to clear all stat modifiers by the key.
     */
    private final FlagArgument<Boolean> clear;

    /**
     * Constructor for the MythicLibStatAction.
     *
     * @param statName    the name of the stat to change
     * @param value       the value to set the stat to
     * @param modifierKey the key of the stat modifier
     * @param type        the type of the stat modifier
     * @param slot        the slot of the stat modifier
     * @param source      the source of the stat modifier
     * @param add         the add flag
     * @param remove      the remove flag
     * @param clear       the clear flag
     */
    public MythicLibStatAction(@Nullable final Argument<String> statName, final Argument<Number> value, final Argument<String> modifierKey,
                               final Argument<ModifierType> type, final Argument<EquipmentSlot> slot,
                               final Argument<ModifierSource> source, final FlagArgument<Boolean> add,
                               final FlagArgument<Boolean> remove, final FlagArgument<Boolean> clear) {
        this.statName = statName;
        this.value = value;
        this.modifierKey = modifierKey;
        this.type = type;
        this.slot = slot;
        this.source = source;
        this.add = add;
        this.remove = remove;
        this.clear = clear;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final MMOPlayerData data = MMOPlayerData.get(profile.getPlayerUUID());
        final String key = modifierKey.getValue(profile);
        if (clear.getValue(profile).orElse(false)) {
            clearModifiers(data, key);
        }

        if (statName == null) {
            return;
        }
        final String stat = statName.getValue(profile);
        if (remove.getValue(profile).orElse(false)) {
            removeModifier(data, key, stat);
        }

        final double statValue = value.getValue(profile).doubleValue();
        final ModifierType modifierType = type.getValue(profile);
        final EquipmentSlot equipmentSlot = slot.getValue(profile);
        final ModifierSource modifierSource = source.getValue(profile);
        if (add.getValue(profile).orElse(false)) {
            addModifier(modifierType, key, stat, statValue, data, modifierSource, equipmentSlot);
        }
    }

    private void removeModifier(final MMOPlayerData playerData, final String key, final String stat) {
        playerData.getStatMap().getInstance(stat).removeIf(key::equals);
    }

    private void clearModifiers(final MMOPlayerData playerData, final String key) {
        for (final StatInstance instance : playerData.getStatMap().getInstances()) {
            instance.removeIf(key::equals);
        }
    }

    private void addModifier(final ModifierType modifierType, final String key, final String stat, final double statValue,
                             final MMOPlayerData data, final ModifierSource modifierSource, final EquipmentSlot equipmentSlot) {
        new StatModifier(key, stat, statValue, modifierType, equipmentSlot, modifierSource).register(data);
    }
}
