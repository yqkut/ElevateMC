package com.elevatemc.potpvp.lobby.listener;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.follow.command.UnfollowCommand;
import com.elevatemc.potpvp.lobby.LobbyHandler;
import com.elevatemc.potpvp.lobby.LobbyItems;
import com.elevatemc.potpvp.lobby.menu.SpectateMenu;
import com.elevatemc.potpvp.lobby.menu.StatisticsMenu;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.match.MatchState;
import com.elevatemc.potpvp.util.ItemListener;
import com.elevatemc.potpvp.validation.PotPvPValidation;
import com.elevatemc.elib.eLib;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public final class LobbyItemListener extends ItemListener {

    private final Map<UUID, Long> canUseRandomSpecItem = new HashMap<>();

    public LobbyItemListener(LobbyHandler lobbyHandler) {
        addHandler(LobbyItems.CREATE_TEAM, player -> player.performCommand("team create"));

        addHandler(LobbyItems.HOST_EVENTS, player -> player.performCommand("host"));

        addHandler(LobbyItems.SPECTATE_MENU_ITEM, player -> {
            if (PotPvPValidation.canUseSpectateItemIgnoreMatchSpectating(player)) {
                new SpectateMenu().openMenu(player);
            }
        });

        addHandler(LobbyItems.SPECTATE_RANDOM_ITEM, player -> {
            MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();

            if (!PotPvPValidation.canUseSpectateItemIgnoreMatchSpectating(player)) {
                return;
            }

            if (canUseRandomSpecItem.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis()) {
                player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Please wait before doing this again!");
                return;
            }

            List<Match> matches = new ArrayList<>(matchHandler.getHostedMatches());
            matches.removeIf(m -> m.isSpectator(player.getUniqueId()) || m.getState() == MatchState.ENDING);

            if (matches.isEmpty()) {
                player.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "There are no matches available to spectate.");
            } else {
                Match currentlySpectating = matchHandler.getMatchSpectating(player);
                Match newSpectating = matches.get(PotPvPSI.RANDOM.nextInt(matches.size()));

                if (currentlySpectating != null) {
                    currentlySpectating.removeSpectator(player, false);
                }

                newSpectating.addSpectator(player, null);
                canUseRandomSpecItem.put(player.getUniqueId(), System.currentTimeMillis() + 3_000L);
            }
        });

        addHandler(LobbyItems.PLAYER_STATISTICS, player -> new StatisticsMenu(player).openMenu(player));

        addHandler(LobbyItems.UNFOLLOW_ITEM, UnfollowCommand::unfollow);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        canUseRandomSpecItem.remove(event.getPlayer().getUniqueId());
    }

}