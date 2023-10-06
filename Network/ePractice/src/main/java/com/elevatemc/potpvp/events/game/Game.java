package com.elevatemc.potpvp.events.game;

import com.elevatemc.elib.util.TaskUtil;
import com.elevatemc.potpvp.arena.Arena;
import com.elevatemc.potpvp.events.EventHandler;
import com.elevatemc.potpvp.events.bukkit.event.GameStateChangeEvent;
import com.elevatemc.potpvp.events.bukkit.event.PlayerJoinGameEvent;
import com.elevatemc.potpvp.events.event.GameEvent;
import com.elevatemc.potpvp.events.event.GameEventLogic;
import com.elevatemc.potpvp.events.event.impl.brackets.BracketsGameEventLogic;
import com.elevatemc.potpvp.events.event.impl.lms.LastManStandingGameEventLogic;
import com.elevatemc.potpvp.events.event.impl.sumo.SumoGameEventLogic;
import com.elevatemc.potpvp.events.parameter.GameParameterOption;
import com.elevatemc.potpvp.events.util.team.GameTeam;
import com.elevatemc.potpvp.events.util.team.GameTeamSizeParameter;
import com.elevatemc.potpvp.util.Color;
import com.elevatemc.potpvp.util.PatchedPlayerUtils;
import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Game {

    private final GameEvent event;
    private final Player host;
    private final List<GameParameterOption> parameters;

    private GameState state = GameState.QUEUED;
    private long startingAt = 0L;
    private final Set<Player> players = new HashSet<>();
    private final GameEventLogic logic;
    private final Set<Player> spectators = new HashSet<>();
    private Arena arena;

    public Game(GameEvent event, Player host,  List<GameParameterOption> parameters) {
        this.event = event;
        this.host = host;
        this.parameters = parameters;
        this.logic = event.getLogic(this);
    }

    public void addSpectator(Player player) {
        if(state == GameState.ENDED) return;

        spectators.add(player);
        players.add(player);

        sendMessage("&d" + PatchedPlayerUtils.getFormattedName(player.getUniqueId()) + " &7is now spectating.");

        reset(player);

        // look at if statement once im done
        player.teleport(arena.getSpectatorSpawn());
    }

    public void add(Player player) {
        final Game other = GameQueue.INSTANCE.getCurrentGame(player);
        if(other != null) return;
        if(state != GameState.STARTING) return;

        players.add(player);
        sendMessage(Color.translate("&d" + PatchedPlayerUtils.getFormattedName(player.getUniqueId()) + " &7joined the event."));
        reset(player);
        Bukkit.getPluginManager().callEvent(new PlayerJoinGameEvent(player, this));
    }

    private void resetSpectator(Player player) {
        player.getInventory().clear();
        player.getInventory().setHeldItemSlot(0);
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().addItem(event.getLobbyItems().toArray(new ItemStack[0]));
        player.getInventory().setItem(8, EventHandler.getLeaveItem());
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);

        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

        // make it so alive players cannot see the spectators
        players.forEach(p -> p.hidePlayer(player));

        // show game players to spectator
        players.forEach(player::showPlayer);

        player.updateInventory();
    }

    public void reset(Player player) {
        if(spectators.contains(player)) {
            resetSpectator(player);
            return;
        }

        player.teleport(arena.getSpectatorSpawn().clone().add(0.0D, -1.0D, 0.0D));

        PatchedPlayerUtils.resetInventory(player, GameMode.SURVIVAL, false);
        player.getInventory().setHeldItemSlot(0);
        player.getInventory().addItem(event.getLobbyItems().toArray(new ItemStack[0]));
        player.getInventory().setItem(8, EventHandler.getLeaveItem());
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }

    public void start() {
        if(!event.canStart(this)) {
            end(true);
            sendMessage("&cCould not start the match. Sending you back to the lobby.");
            return;
        }

        arena.takeSnapshot();
        logic.start();

        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(this, GameState.RUNNING));
    }

    public void end(boolean... failed) {
        if(failed.length == 0) arena.restore();
        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(this, GameState.ENDED));
    }

    public List<Location> getFirstSpawnLocations() {
        if(getParameter(GameTeamSizeParameter.Duos.class) != null) {
            final Vector direction = arena.getTeam1Spawn().getDirection();
            return ImmutableList.of(
                    arena.getTeam1Spawn().add(direction.clone().setX(-direction.getZ()).setZ(direction.getX())),
                    arena.getTeam1Spawn().add(direction.clone().setX(direction.getZ()).setZ(-direction.getX()))
            );
        } else {
            return ImmutableList.of(arena.getTeam1Spawn());
        }
    }

    public List<Location> getSecondSpawnLocations() {
        if(getParameter(GameTeamSizeParameter.Duos.class) != null) {
            final Vector direction = arena.getTeam2Spawn().getDirection();
            return ImmutableList.of(
                    arena.getTeam2Spawn().add(direction.clone().setX(-direction.getZ()).setZ(direction.getX())),
                    arena.getTeam2Spawn().add(direction.clone().setX(direction.getZ()).setZ(-direction.getX()))
            );
        } else {
            return ImmutableList.of(arena.getTeam2Spawn());
        }
    }

    public void sendMessage(String... message) {
        for(String msg : message) {
            players.forEach(player -> player.sendMessage(Color.translate(msg)));
        }
    }

    public GameParameterOption getParameter(Class<?> clazz) {
        for(GameParameterOption parameter : parameters) {
            if(parameter.getClass() == clazz || clazz.isAssignableFrom(parameter.getClass())) {
                return (GameParameterOption) clazz.cast(parameter);
            }
        }

        return null;
    }

    public int getMaxPlayers() {
        if(logic instanceof LastManStandingGameEventLogic) {
            if(getParameter(GameTeamSizeParameter.Duos.class) != null) {
                return arena.getEventSpawns().size() * 2;
            } else {
                return arena.getEventSpawns().size();
            }
        }

        if(logic instanceof SumoGameEventLogic) {
            if(getParameter(GameTeamSizeParameter.Duos.class) != null) {
                return 32;
            } else {
                return 16;
            }
        }

        return -1;
    }
}
