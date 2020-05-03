package pl.betoncraft.betonquest.commands.quest;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.InternalMessagekeys;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.id.PlayerID;

@CommandAlias("quest")
public class ConditionsCommand extends BaseCommand {
    @Subcommand("conditions|condition|c")
    private void handleConditions(CommandIssuer sender, @Flags("nullable,name") PlayerID target, ConditionID id) {
        sender.sendInfo(InternalMessagekeys.PLAYER_CONDITION,
                "{tag}", (id.inverted() ? "! " : "") + id.generateInstruction().getInstruction(),
                "{value}", Boolean.toString(BetonQuest.condition(target.getPlayerID(), id)));
    }
}
