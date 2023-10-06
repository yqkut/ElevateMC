package com.elevatemc.potpvp.events.task;

import com.elevatemc.elib.util.TaskUtil;
import com.elevatemc.elib.util.TimeUtils;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.Arena;
import com.elevatemc.potpvp.events.bukkit.event.GameStateChangeEvent;
import com.elevatemc.potpvp.events.game.Game;
import com.elevatemc.potpvp.events.game.GameState;
import com.elevatemc.potpvp.events.parameter.GameParameterOption;
import com.elevatemc.potpvp.util.Color;
import com.elevatemc.potpvp.util.PatchedPlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GameStartTask {

    private final long startsAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(45) - 5;
    private final int interval = 15;

    private final PotPvPSI plugin;
    private final Game game;

    public GameStartTask(PotPvPSI plugin, Game game) {
        this.plugin = plugin;
        this.game = game;

        final Optional<Arena> arena = plugin.getArenaHandler().allocateUnusedArena(schem -> schem.getEvent() == game.getEvent() && schem.isEnabled());
        if(arena.isPresent()) {
            Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(game, GameState.STARTING));
            game.setStartingAt(startsAt);
            game.setArena(arena.get());
            new Task(game).runTaskTimer(plugin, 0, interval * 20L);
        } else {
            Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(game, GameState.ENDED));
            Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(player -> {
                player.sendMessage(Color.translate("&cFailed to start " + game.getEvent().getName() + " due to lack of an arena."));
            });
        }
    }

    private final class Task extends BukkitRunnable {
        private final Game game;

        public Task(Game game) {
            this.game = game;
        }

        @Override
        public void run() {
            if(startsAt <= System.currentTimeMillis() || game.getPlayers().size() == game.getMaxPlayers()) {
                TaskUtil.runSync(game::start);
                cancel();
                return;
            }

            Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(new String[]{"",
                    ChatColor.GRAY + "███████",
                    ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█████" + ChatColor.GRAY + "█" + " " + ChatColor.DARK_AQUA + "[" + game.getEvent().getName() + " Event]",
                    ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█████" + " " + PatchedPlayerUtils.getFormattedName(game.getHost().getUniqueId()) + ChatColor.GRAY + " is hosting an event!",
                    ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "████" + ChatColor.GRAY + "██" + " " + ChatColor.GRAY + "Starts in " + ChatColor.AQUA + (TimeUtils.formatIntoDetailedString((int)((startsAt + 500 - System.currentTimeMillis()) / 1000))),
                    ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█████" + " " + ChatColor.GRAY + "Join with the " + ChatColor.AQUA + "diamond " + ChatColor.GRAY + "in your hotbar.",
                    ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█████" + ChatColor.GRAY + "█" + " " + ChatColor.GRAY + ChatColor.ITALIC + "Event Type: (" + game.getParameters().stream().map(GameParameterOption::getDisplayName).collect(Collectors.joining(", ")) + ")",
                    ChatColor.GRAY + "███████",
                    ""}));
        }
    }
}
