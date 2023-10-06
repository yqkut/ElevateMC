package com.elevatemc.potpvp.events.util.team;

import com.elevatemc.elib.util.PlayerUtils;
import com.elevatemc.potpvp.util.PatchedPlayerUtils;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class GameTeam {

    private List<Player> players;

    private List<Player> died = new ArrayList<>();
    private int round = 0;
    private int kills = 0;
    private boolean fighting = false;
    private boolean starting = false;

    public GameTeam(List<Player> players) {
        this.players = players;
    }

    public boolean isFinished() {
        return died.size() == players.size();
    }

    public void died(Player player) {
        if(!died.contains(player)) {
            died.add(player);
        }
    }

    public boolean hasDied(Player player) {
        return died.contains(player);
    }

    public void reset() {
        died.clear();
        fighting = false;
        starting = false;
    }

    public String getName() {
        return StringUtils.join(players.stream().map(player -> PatchedPlayerUtils.getFormattedName(player.getUniqueId())).collect(Collectors.toList()), ChatColor.YELLOW + " + ");
    }

    public int getPing() {
        return PlayerUtils.getPing(players.get(0));
    }

    public List<Player> getOthers(Player player) {
        return getPlayers()
                .stream()
                .filter(other -> !other.getUniqueId().equals(player.getUniqueId()))
                .collect(Collectors.toList());
    }

    public void showTeamsPlayers(GameTeam team) {
        getPlayers().forEach(thisPlayer -> team.getPlayers().forEach(thisPlayer::showPlayer));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameTeam gameTeam = (GameTeam) o;
        return round == gameTeam.round && kills == gameTeam.kills && fighting == gameTeam.fighting && starting == gameTeam.starting && Objects.equals(players, gameTeam.players) && Objects.equals(died, gameTeam.died);
    }

    @Override
    public int hashCode() {
        return Objects.hash(players);
    }
}
