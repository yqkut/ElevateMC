package com.elevatemc.potpvp.tournament.menu;

import com.google.common.base.Preconditions;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.validation.PotPvPValidation;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

final class StatusButton extends Button {

    private final Match match;

    StatusButton(Match match) {
        this.match = Preconditions.checkNotNull(match, "match");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + match.getSimpleDescription(false);
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();
        MatchTeam teamA = match.getTeams().get(0);
        MatchTeam teamB = match.getTeams().get(1);
        description.add(ChatColor.DARK_AQUA + "Arena: " + ChatColor.WHITE + PotPvPSI.getInstance().getArenaHandler().getSchematic(match.getArena().getSchematic()).getDisplayName());

        List<UUID> spectators = new ArrayList<>(match.getSpectators());
        // don't count actual players and players in silent mode.
        spectators.removeIf(uuid -> Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).hasMetadata("modmode") || match.getPreviousTeam(uuid) != null);

        if (teamA.getAliveMembers().size() != 1 || teamB.getAliveMembers().size() != 1) {
            description.add("");

            for (UUID member : teamA.getAliveMembers()) {
                description.add(ChatColor.AQUA + UUIDUtils.name(member));
            }

            description.add(ChatColor.DARK_AQUA + "   vs.");

            for (UUID member : teamB.getAliveMembers()) {
                description.add(ChatColor.AQUA + UUIDUtils.name(member));
            }
        }

        description.add("");
        description.add(ChatColor.GREEN + "» Click to spectate «");

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return match.getGameMode().getIcon().getItemType();
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        if (!PotPvPValidation.canUseSpectateItemIgnoreMatchSpectating(player)) {
            return;
        }

        Match currentlySpectating = PotPvPSI.getInstance().getMatchHandler().getMatchSpectating(player);

        if (currentlySpectating != null) {
            currentlySpectating.removeSpectator(player, false);
        }

        match.addSpectator(player, null);
    }

}