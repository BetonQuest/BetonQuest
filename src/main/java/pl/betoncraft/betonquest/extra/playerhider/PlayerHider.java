package pl.betoncraft.betonquest.extra.playerhider;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.*;

/**
 * The PlayerHider can hide other players, if the source fits all conditions and the targets also fits there conditions.
 */
public class PlayerHider {
    /**
     * Players that previously had collision enabled
     */
    private static final List<UUID> PLAYERS_WITH_COLLISION = new ArrayList<>();

    /**
     * The map's key is an array containing the source player's conditions
     * and the map's value is an array containing the target player's conditions.
     */
    private final Map<ConditionID[], ConditionID[]> hiders;
    /**
     * The running hider
     */
    private final BukkitTask bukkitTask;

    /**
     * Initialize and start a new PlayerHider
     * @throws InstructionParseException Thrown if there is an configuration error.
     */
    public PlayerHider() throws InstructionParseException {
        hiders = new HashMap<>();

        for (final ConfigPackage pack : Config.getPackages().values()) {
            final ConfigurationSection hiderSection = pack.getCustom().getConfig().getConfigurationSection("player_hider");
            if (hiderSection != null) {
                for (final String key : hiderSection.getKeys(false)) {
                    final String rawConditionsSource = hiderSection.getString(key + ".source_player");
                    final String rawConditionsTarget = hiderSection.getString(key + ".target_player");
                    hiders.put(getConditions(pack, key, rawConditionsSource), getConditions(pack, key, rawConditionsTarget));
                }
            }
        }

        final long period = BetonQuest.getInstance().getConfig().getLong("player_hider_check_interval", 20);
        bukkitTask = Bukkit.getScheduler().runTaskTimer(BetonQuest.getInstance(), this::updateVisibility, 1, period);
    }

    /**
     * Stops the running PlayerHider.
     */
    public void stop() {
        bukkitTask.cancel();
    }

    private ConditionID[] getConditions(final ConfigPackage pack, final String key, final String rawConditions) throws InstructionParseException {
        if (rawConditions == null) {
            return new ConditionID[0];
        }
        final String[] rawConditionsList = rawConditions.split(",");
        final ConditionID[] conditionList = new ConditionID[rawConditionsList.length];
        for (int i = 0; i < rawConditionsList.length; i++) {
            try {
                conditionList[i] = new ConditionID(pack, rawConditionsList[i]);
            } catch (final ObjectNotFoundException e) {
                throw new InstructionParseException("Error while loading " + rawConditionsList[i]
                        + " condition for player_hider " + pack.getName() + "." + key + ": " + e.getMessage(), e);
            }
        }
        return conditionList;
    }

    private void updateVisibility() {
        final Map<Player, List<Player>> playersToHide = getPlayersToHide();
        for (final Player source : Bukkit.getOnlinePlayers()) {
            final List<Player> playerToHideList = playersToHide.get(source);
            if (playerToHideList == null) {
                if (PLAYERS_WITH_COLLISION.remove(source.getUniqueId())) {
                    source.setCollidable(true);
                }
                for (final Player target : Bukkit.getOnlinePlayers()) {
                    source.showPlayer(BetonQuest.getInstance(), target);
                }
            } else {
                if (source.isCollidable()) {
                    PLAYERS_WITH_COLLISION.add(source.getUniqueId());
                    source.setCollidable(false);
                }
                for (final Player target : Bukkit.getOnlinePlayers()) {
                    if(playerToHideList.contains(target)) {
                        source.hidePlayer(BetonQuest.getInstance(), target);
                    } else {
                        source.showPlayer(BetonQuest.getInstance(), target);
                    }
                }
            }
        }
    }

    private Map<Player, List<Player>> getPlayersToHide() {
        final Map<Player, List<Player>> playersToHide = new HashMap<>();
        for (final Map.Entry<ConditionID[], ConditionID[]> hider : hiders.entrySet()) {
            final List<Player> targetPlayers = new ArrayList<>();
            for (final Player target : Bukkit.getOnlinePlayers()) {
                if (BetonQuest.conditions(PlayerConverter.getID(target), hider.getValue())) {
                    targetPlayers.add(target);
                }
            }
            for (final Player source : Bukkit.getOnlinePlayers()) {
                if (!BetonQuest.conditions(PlayerConverter.getID(source), hider.getKey())) {
                    continue;
                }
                final List<Player> hiddenPlayers = getOrCreatePlayerList(source, playersToHide);
                hiddenPlayers.addAll(targetPlayers);
                hiddenPlayers.remove(source);
                playersToHide.put(source, hiddenPlayers);
            }
        }
        return playersToHide;
    }

    private List<Player> getOrCreatePlayerList(final Player player, final Map<Player, List<Player>> playersToHide) {
        final List<Player> playList = playersToHide.get(player);
        if (playList == null) {
            return new ArrayList<>();
        } else {
            return playList;
        }
    }
}
