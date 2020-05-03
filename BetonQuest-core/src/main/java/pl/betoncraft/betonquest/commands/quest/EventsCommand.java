package pl.betoncraft.betonquest.commands.quest;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.InternalMessagekeys;
import pl.betoncraft.betonquest.id.EventID;
import pl.betoncraft.betonquest.id.PlayerID;

@CommandAlias("quest")
public class EventsCommand extends BaseCommand {
    @Subcommand("events|event|e")
    public void handleEvents(CommandIssuer sender, @Flags("nullable,name") PlayerID target, EventID id) {
        BetonQuest.event(target.getPlayerID(), id);
        sender.sendInfo(InternalMessagekeys.PLAYER_EVENT, "{event}", id.generateInstruction().getInstruction());
    }
}
